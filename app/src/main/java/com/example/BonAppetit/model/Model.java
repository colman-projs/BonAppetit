package com.example.BonAppetit.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.BonAppetit.MyApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    public static final Model instance = new Model();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());


    public interface GetUserLoginListener {
        void onComplete(User user);
    }

    public void getUserLogin(String mail, String password, GetUserLoginListener listener) {
        modelFirebase.getUserLogin(mail, password, listener);
    }


    public interface AddListener {
        void onComplete();
    }

    public void registerUser(User user, AddListener listener) {
        modelFirebase.registerUser(user, () -> {
            listener.onComplete();
        });
    }
//--------------------------------------------------------------------------------------------------

    public enum StudentListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<StudentListLoadingState> studentListLoadingState = new MutableLiveData<StudentListLoadingState>();

    public LiveData<StudentListLoadingState> getStudentListLoadingState() {
        return studentListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private Model() {
        studentListLoadingState.setValue(StudentListLoadingState.loaded);
    }

    MutableLiveData<List<Student>> studentsList = new MutableLiveData<List<Student>>();

    public LiveData<List<Student>> getAll() {
        if (studentsList.getValue() == null) {
            refreshStudentList();
        }

        return studentsList;
    }

    public void refreshStudentList() {
        studentListLoadingState.setValue(StudentListLoadingState.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("StudentsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Student> stList = AppLocalDb.db.studentDao().getAll();
            studentsList.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllStudents(lastUpdateDate, new ModelFirebase.GetAllStudentsListener() {
            @Override
            public void onComplete(List<Student> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Student student : list) {
                            AppLocalDb.db.studentDao().insertAll(student);
                            if (lud < student.getUpdateDate()) {
                                lud = student.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("StudentsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Student> stList = AppLocalDb.db.studentDao().getAll();
                        studentsList.postValue(stList);
                        studentListLoadingState.postValue(StudentListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public interface AddStudentListener {
        void onComplete();
    }

    public void addStudent(Student student, AddStudentListener listener) {
        modelFirebase.addStudent(student, () -> {
            listener.onComplete();
            refreshStudentList();
        });
    }

    public interface GetStudentById {
        void onComplete(Student student);
    }

    public void getStudentById(String studentId, GetStudentById listener) {
        modelFirebase.getStudentById(studentId, listener);
//        return null;
    }


    public interface SaveImageListener {
        void onComplete(String url);
    }

    public void saveUserImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, "user_avatars/", imageName, listener);
    }

    /**
     * Authentication
     */

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }
}