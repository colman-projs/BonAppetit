package com.example.BonAppetit.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class RestaurantType {
    final public static String COLLECTION_NAME = "restaurant_type";

    @PrimaryKey
    @NonNull
    String id;
    String name = "";
    String imageUrl;
    boolean deleted = false;
    Long updateDate = new Long(0);
    boolean isChecked = false;

    public RestaurantType() {}
    public RestaurantType(String name) {
        this.name = name;
    }

    public RestaurantType(String id, boolean isChecked) {
        this.id = id;
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.isChecked = deleted;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
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
        json.put("name", name);
        json.put("imageUrl", imageUrl);
        json.put("deleted", deleted);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static RestaurantType create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String name = (String) json.get("name");
        String imageUrl = (String)json.get("imageUrl");
        boolean deleted = (boolean)json.get("deleted");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts == null ? 0 : ts.getSeconds();

        RestaurantType restaurantType = new RestaurantType(name);
        restaurantType.setId(id);
        restaurantType.setImageUrl(imageUrl);
        restaurantType.setDeleted(deleted);
        restaurantType.setUpdateDate(updateDate);
        return restaurantType;
    }
}
