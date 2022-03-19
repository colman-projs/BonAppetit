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

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    String name = "";
    String avatarUrl;
    boolean deleted = false;
    Long updateDate = new Long(0);

    public RestaurantType() {}
    public RestaurantType(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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
        json.put("name", name);
        json.put("avatarUrl", avatarUrl);
        json.put("deleted", deleted);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static RestaurantType create(Map<String, Object> json) {
        int id = (int) json.get("id");
        String name = (String) json.get("name");
        String avatarUrl = (String)json.get("avatarUrl");
        boolean deleted = (boolean)json.get("deleted");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        RestaurantType restaurantType = new RestaurantType(name);
        restaurantType.setId(id);
        restaurantType.setAvatarUrl(avatarUrl);
        restaurantType.setDeleted(deleted);
        restaurantType.setUpdateDate(updateDate);
        return restaurantType;
    }
}
