package com.example.BonAppetit.model;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModelFirebase() {
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
                .addOnCompleteListener(task -> {
                    List<User> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            User user = User.create(doc.getData());
                            list.add(user);
                        }
                    }
                    listener.onComplete(list.get(0));
                });
    }

    public void getUserById(String id, Model.GetUserLoginListener listener) {
        db.collection(User.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    List<User> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            User user = User.create(doc.getData());
                            list.add(user);
                        }
                    }
                    listener.onComplete(list.get(0));
                });
    }

    public void getIfUserExists(String email, Model.GetUserExistsListener listener) {
        db.collection(User.COLLECTION_NAME)
                .whereEqualTo("mail", email)
                .get()
                .addOnCompleteListener(task -> listener.onComplete(task.isSuccessful() & (task.getResult() != null)));
    }

    public void registerUser(User user, Model.AddListener listener) {
        Map<String, Object> json = user.toJson();
        db.collection(User.COLLECTION_NAME)
                .add(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void getRestaurantById(String restaurantId, Model.GetRestaurantById listener) {
        db.collection(Restaurant.COLLECTION_NAME)
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    Restaurant restaurant = null;
                    if (task.isSuccessful() & task.getResult()!= null){
                        restaurant = Restaurant.create(Objects.requireNonNull(task.getResult().getData()));
                    }
                    listener.onComplete(restaurant);
                });
    }

    public interface GetAllRestaurantsListener {
        void onComplete(List<Restaurant> list);
    }

    public interface GetAllRestaurantsReviewsListener {
        void onComplete(List<Review> list);
    }

    public interface GetAllRestaurantsTypesListener {
        void onComplete(List<RestaurantType> list);
    }

    public void getAllRestaurants(Long lastUpdateDate, GetAllRestaurantsListener listener) {
        db.collection(Restaurant.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate", new Timestamp(lastUpdateDate, 0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Restaurant> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Restaurant restaurant = Restaurant.create(doc.getData());
                            list.add(restaurant);

                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getAllRestaurantReviews(Long lastUpdateDate, GetAllRestaurantsReviewsListener listener) {
        db.collection(Review.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate", new Timestamp(lastUpdateDate, 0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Review> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Review review = Review.create(doc.getData());
                            list.add(review);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getReviewsByRestaurant(Long lastUpdateDate, String restaurantId, GetAllRestaurantsReviewsListener listener) {
        db.collection(Review.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate", new Timestamp(lastUpdateDate, 0))
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    List<Review> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Review review = Review.create(doc.getData());
                            list.add(review);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getAllRestaurantTypes(Long lastUpdateDate, GetAllRestaurantsTypesListener listener) {
        db.collection(RestaurantType.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate", new Timestamp(lastUpdateDate, 0))
                .get()
                .addOnCompleteListener(task -> {
                    List<RestaurantType> list = new LinkedList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            RestaurantType type = RestaurantType.create(doc.getData());
                            list.add(type);
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

    public void addRestaurantReview(Review review, Model.AddListener listener) {
        DocumentReference documentReference = db.collection(Review.COLLECTION_NAME)
                .document();
        review.setId(documentReference.getId());
        Map<String, Object> json = review.toJson();
        documentReference.set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void updateRestaurant(Restaurant restaurant, Model.AddListener listener) {
        Map<String, Object> json = restaurant.toJson();
        db.collection(Restaurant.COLLECTION_NAME)
                .document(restaurant.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void updateReview(Review review, Model.AddListener listener) {
        Map<String, Object> json = review.toJson();
        db.collection(Restaurant.COLLECTION_NAME)
                .document(review.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void getReviewById(String reviewId, Model.getReviewById listener) {
        db.collection(Review.COLLECTION_NAME)
                .document(reviewId)
                .get()
                .addOnCompleteListener(task -> {
                    Review review = null;
                    if (task.isSuccessful() & task.getResult() != null) {
                        review = Review.create(Objects.requireNonNull(task.getResult().getData()));
                    }
                    listener.onComplete(review);
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
                .addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> listener.onComplete(uri.toString())));
    }

    /**
     * Authentication
     */
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

}
