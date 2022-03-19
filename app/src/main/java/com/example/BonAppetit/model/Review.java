package com.example.BonAppetit.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Review {
    final public static String COLLECTION_NAME = "reviews";

    @PrimaryKey
    @NonNull
    String id;
    int restaurantId;
    int userId;
    String description = "";
    int rating;
    String imageUrl;
    boolean deleted = false;
    Long updateDate = new Long(0);

    public Review() {}
    public Review(int restaurantId, int userId, String description, int rating) {
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.description = description;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id);
        json.put("restaurantId", restaurantId);
        json.put("userId", userId);
        json.put("description", description);
        json.put("rating", rating);
        json.put("imageUrl", imageUrl);
        json.put("deleted", deleted);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Review create(Map<String, Object> json) {
        String id = (String) json.get("id");
        int restaurantId = (int) json.get("restaurantId");
        int userId = (int) json.get("userId");
        String description = (String)json.get("description");
        int rating = (int)json.get("rating");
        String imageUrl = (String)json.get("imageUrl");
        boolean deleted = (boolean)json.get("deleted");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        Review review = new Review(restaurantId, userId, description, rating);
        review.setId(id);
        review.setImageUrl(imageUrl);
        review.setDeleted(deleted);
        review.setUpdateDate(updateDate);
        return review;
    }
}
