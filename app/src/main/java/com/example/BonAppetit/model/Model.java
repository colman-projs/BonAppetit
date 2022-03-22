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
        loadingState.setValue(LoadingStates.loaded);
    }

    private void onCompleteGetAllRestaurants(List<Restaurant> list) {
        // add all records to the local db
        executor.execute(() -> {
            Long lud = Long.valueOf(0);
            Log.d("TAG", "restaurants returned " + list.size());
            for (Restaurant restaurant : list) {
                if (restaurant.isDeleted()) {
                    AppLocalDb.db.restaurantDao().delete(restaurant);
                } else {
                    AppLocalDb.db.restaurantDao().insertAll(restaurant);
                }
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
            loadingState.postValue(LoadingStates.loaded);
        });
    }

    private void onCompleteGetAllRestaurantTypes(List<RestaurantType> list) {
        // add all records to the local db
        executor.execute(() -> {
            Long lud = Long.valueOf(0);
            Log.d("TAG", "types returned " + list.size());
            for (RestaurantType restaurant : list) {
                if (restaurant.isDeleted()) {
                    AppLocalDb.db.restaurantTypeDao().delete(restaurant);
                } else {
                    AppLocalDb.db.restaurantTypeDao().insertAll(restaurant);
                }
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
            List<RestaurantType> stList = AppLocalDb.db.restaurantTypeDao().getAll();
            restaurantTypes.postValue(stList);
            loadingState.postValue(LoadingStates.loaded);
        });
    }

    private void onCompleteGetAllRestaurantReviews(List<Review> list) {
// add all records to the local db
        executor.execute(() -> {
            Long lud = Long.valueOf(0);
            Log.d("TAG", "reviews returned " + list.size());
            for (Review review : list) {
                if (review.isDeleted()) {
                    AppLocalDb.db.reviewDao().delete(review);
                } else {
                    AppLocalDb.db.reviewDao().insertAll(review);
                }
                if (lud < review.getUpdateDate()) {
                    lud = review.getUpdateDate();
                }
            }
// update last local update date
            MyApplication.getContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("ReviewsLastUpdateDate", lud)
                    .commit();

//return all data to caller
            List<Review> stList = AppLocalDb.db.reviewDao().getAll();
            restaurantReviews.postValue(stList);
            loadingState.postValue(LoadingStates.loaded);
        });
    }

    private void onCompleteGetReviewsByRestaurant(List<Review> list) {
// add all records to the local db
        executor.execute(() -> {
            Long lud = Long.valueOf(0);
            Log.d("TAG", "reviews by restaurant returned " + list.size());
            for (Review review : list) {
                if (review.isDeleted()) {
                    AppLocalDb.db.reviewDao().delete(review);
                } else {
                    AppLocalDb.db.reviewDao().insertAll(review);
                }
                if (lud < review.getUpdateDate()) {
                    lud = review.getUpdateDate();
                }
            }
// update last local update date
            MyApplication.getContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("ReviewsLastUpdateDate", lud)
                    .commit();

//return all data to caller
            List<Review> stList = AppLocalDb.db.reviewDao().getByRestaurant(lastRestaurantId);
            restaurantReviews.postValue(stList);
            loadingState.postValue(LoadingStates.loaded);
        });
    }

    public interface GetUserLoginListener {
        void onComplete(User user);
    }

    public interface GetUserByIdListener {
        void onComplete(User user);
    }

    public void getUserLogin(String mail, String password, GetUserLoginListener listener) {
        modelFirebase.getUserLogin(mail, password, listener);
    }

    public void getUserById(String id, GetUserByIdListener listener) {
        modelFirebase.getUserById(id, listener);
    }

    public interface GetUserExistsListener {
        void onComplete(boolean exists);
    }

    public void getIfUserExists(String email, Model.GetUserExistsListener listener) {
        modelFirebase.getIfUserExists(email, listener);
    }

    public void registerUser(User user, AddListener listener) {
        modelFirebase.registerUser(user, () -> listener.onComplete());
    }

    public enum LoadingStates {
        loading,
        loaded
    }

    MutableLiveData<LoadingStates> loadingState = new MutableLiveData<>();

    public LiveData<LoadingStates> getListLoadingState() {
        return loadingState;
    }

    MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    MutableLiveData<List<Review>> restaurantReviews = new MutableLiveData<>();
    MutableLiveData<List<RestaurantType>> restaurantTypes = new MutableLiveData<>();
    String lastRestaurantId = "";

    public LiveData<List<Restaurant>> getAll() {
        if (restaurantList.getValue() == null) {
            refreshRestaurantList();
        }

        return restaurantList;
    }

    public LiveData<List<RestaurantType>> getAllTypes() {
        if (restaurantTypes.getValue() == null) {
            refreshRestaurantTypes();
        }

        return restaurantTypes;
    }

    public LiveData<List<Review>> getAllReviews() {
        if (restaurantReviews.getValue() == null) {
            refreshRestaurantReviews();
        }

        return restaurantReviews;
    }

    public LiveData<List<Review>> getReviewsByRestaurant(String restaurantId) {
        if (restaurantReviews.getValue() == null || lastRestaurantId != restaurantId) {
            lastRestaurantId = restaurantId;
            refreshRestaurantReviews(restaurantId);
        }

        return restaurantReviews;
    }

    public void refreshRestaurantList() {
        loadingState.setValue(LoadingStates.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("RestaurantsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Restaurant> stList = AppLocalDb.db.restaurantDao().getAll();
            restaurantList.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllRestaurants(lastUpdateDate, this::onCompleteGetAllRestaurants);
    }

    public void refreshRestaurantTypes() {
        loadingState.setValue(LoadingStates.loading);

        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("RestaurantTypesLastUpdateDate", 0);

        executor.execute(() -> {
            List<RestaurantType> stList = AppLocalDb.db.restaurantTypeDao().getAll();
            restaurantTypes.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllRestaurantTypes(lastUpdateDate, this::onCompleteGetAllRestaurantTypes);
    }

    public void refreshRestaurantReviews() {
        loadingState.setValue(LoadingStates.loading);

        // get last local update date
        Long reviewsLastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("ReviewsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Review> stList = AppLocalDb.db.reviewDao().getAll();
            restaurantReviews.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllRestaurantReviews(reviewsLastUpdateDate,
                this::onCompleteGetAllRestaurantReviews);
    }

    public void refreshRestaurantReviews(String restaurantId) {
        loadingState.setValue(LoadingStates.loading);

        // get last local update date
        Long reviewsLastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("ReviewsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Review> stList = AppLocalDb.db.reviewDao().getByRestaurant(restaurantId);
            restaurantReviews.postValue(stList);
        });

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllRestaurantReviews(reviewsLastUpdateDate,
                this::onCompleteGetReviewsByRestaurant);
    }

    public void addRestaurant(Restaurant restaurant, AddListener listener) {
        modelFirebase.addRestaurant(restaurant, () -> {
            listener.onComplete();
            refreshRestaurantList();
        });
    }

    public void addRestaurantReview(Review review, AddListener listener) {
        modelFirebase.addRestaurantReview(review, () -> {
            listener.onComplete();
            refreshRestaurantReviews();
        });
    }

    public void updateRestaurant(Restaurant restaurant, AddListener listener) {
        modelFirebase.updateRestaurant(restaurant, () -> {
            listener.onComplete();
            refreshRestaurantList();
        });
    }

    public void updateReview(Review review, AddListener listener) {
        modelFirebase.updateReview(review, () -> {
            listener.onComplete();
            refreshRestaurantReviews();
        });
    }

    public interface AddListener {
        void onComplete();
    }

    public interface getReviewById {
        void onComplete(Review review);
    }

    public interface GetRestaurantById {
        void onComplete(Restaurant restaurant);
    }

    public Student getRestaurantById(String restaurantId, GetRestaurantById listener) {
        modelFirebase.getRestaurantById(restaurantId, listener);
        return null;
    }
//--------------------------------------------------------------------------------------------------

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