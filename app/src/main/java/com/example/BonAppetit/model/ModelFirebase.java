package com.example.BonAppetit.model;

import static java.lang.Integer.parseInt;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModelFirebase(){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void getUserLogin(String mail, String password, Model.GetUserLoginListener listener) {
        db.collection(User.COLLECTION_NAME)
                .whereEqualTo("mail", mail)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> list = new LinkedList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                User user = User.create(doc.getData());
                                if (user != null) {
                                    list.add(user);
                                }
                            }
                        }
                        listener.onComplete(list.get(0));
                    }
                });
    }

    public void registerUser(User user, Model.AddListener listener) {
        Map<String, Object> json = user.toJson();
        db.collection(User.COLLECTION_NAME)
                .add(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public interface GetAllRestaurantsListener{
        void onComplete(List<Restaurant> list);
    }

    public void getAllRestaurants(Long lastUpdateDate, GetAllRestaurantsListener listener) {
        db.collection(Restaurant.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Restaurant> list = new LinkedList<Restaurant>();
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Restaurant restaurant = Restaurant.create(doc.getData());
                            if (restaurant != null){
                                list.add(restaurant);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void addRestaurant(Restaurant restaurant, Model.AddListener listener) {
        DocumentReference documentReference = db.collection(Restaurant.COLLECTION_NAME)
            .document();
        restaurant.setId(documentReference.getId());
        Map<String, Object> json = restaurant.toJson();
        documentReference.set(json)
            .addOnSuccessListener(unused -> listener.onComplete())
            .addOnFailureListener(e -> listener.onComplete());
    }

    public void updateRestaurant(Restaurant restaurant, Model.AddStudentListener listener) {
        Map<String, Object> json = restaurant.toJson();
        db.collection(Restaurant.COLLECTION_NAME)
                .document(restaurant.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public interface GetAllStudentsListener{
        void onComplete(List<Student> list);
    }
    //TODO: fix since...
    public void getAllStudents(Long lastUpdateDate, GetAllStudentsListener listener) {
        db.collection(Student.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Student> list = new LinkedList<Student>();
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Student student = Student.create(doc.getData());
                            if (student != null){
                                list.add(student);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void addStudent(Student student, Model.AddStudentListener listener) {
        Map<String, Object> json = student.toJson();
        db.collection(Student.COLLECTION_NAME)
                .document(student.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void getStudentById(String studentId, Model.GetStudentById listener) {
        db.collection(Student.COLLECTION_NAME)
                .document(studentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Student student = null;
                        if (task.isSuccessful() & task.getResult()!= null){
                            student = Student.create(task.getResult().getData());
                        }
                        listener.onComplete(student);
                    }
                });
    }

    /**
     * Firebase Storage
     */
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public void saveImage(Bitmap imageBitmap, String pathName, String imageName, Model.SaveImageListener listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child(pathName + imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Uri downloadUrl = uri;
                    listener.onComplete(downloadUrl.toString());
                });
            }
        });
    }

    /**
     * Authentication
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isSignedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

}
