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

    ModelFirebase modelFirebase = new ModelFirebase();

    private Model() {
        restaurantListLoadingState.setValue(RestaurantListLoadingState.loaded);
    }

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

    public enum RestaurantListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<RestaurantListLoadingState> restaurantListLoadingState = new MutableLiveData<RestaurantListLoadingState>();

    public LiveData<RestaurantListLoadingState> getRestaurantListLoadingState() {
        return restaurantListLoadingState;
    }

    MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<List<Restaurant>>();

    public LiveData<List<Restaurant>> getAll() {
        if (restaurantList.getValue() == null) {
            refreshRestaurantList();
        }

        return restaurantList;
    }

    public void refreshRestaurantList() {
        restaurantListLoadingState.setValue(RestaurantListLoadingState.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("RestaurantsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Restaurant> stList = AppLocalDb.db.restaurantDao().getAll();
            restaurantList.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllRestaurants(lastUpdateDate, new ModelFirebase.GetAllRestaurantsListener() {
            @Override
            public void onComplete(List<Restaurant> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Restaurant restaurant : list) {
//                            AppLocalDb.db.studentDao().insertAll(student);
                            if (lud < restaurant.getUpdateDate()) {
                                lud = restaurant.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("RestaurantsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Restaurant> stList = AppLocalDb.db.restaurantDao().getAll();
                        restaurantList.postValue(stList);
                        restaurantListLoadingState.postValue(RestaurantListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public void addRestaurant(Restaurant restaurant, AddListener listener) {
        modelFirebase.addRestaurant(restaurant, () -> {
            listener.onComplete();
            refreshRestaurantList();
        });
    }

    public void updateRestaurant(Restaurant restaurant, AddStudentListener listener) {
        modelFirebase.updateRestaurant(restaurant, () -> {
            listener.onComplete();
            refreshRestaurantList();
        });
    }

//--------------------------------------------------------------------------------------------------

    public interface AddStudentListener {
        void onComplete();
    }

    public void addStudent(Student student, AddStudentListener listener) {
        modelFirebase.addStudent(student, () -> {
            listener.onComplete();
            refreshRestaurantList();
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

    public void saveRestaurantImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, "restaurant_images/", imageName, listener);
    }

    public void saveReviewImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, "review_images/", imageName, listener);
    }

    public void saveRestaurantTypeImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, "types_images/", imageName, listener);
    }

    /**
     * Authentication
     */

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }
}