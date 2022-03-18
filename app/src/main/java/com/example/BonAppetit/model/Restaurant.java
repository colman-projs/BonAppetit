package com.example.BonAppetit.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.type.LatLng;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Restaurant {
    final public static String COLLECTION_NAME = "users";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    String name = "";
    String type = "";
    String description = "";
    Double avgRate = 0.0;
    String imageUrl;
    Double latitude = 0.0;
    Double longitude = 0.0;
    boolean deleted = false;
    Long updateDate = new Long(0);

    public Restaurant() {}
    public Restaurant(String name, String type, String description, Double avgRate, Double latitude, Double longitude) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.avgRate = avgRate;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(Double avgRate) {
        this.avgRate = avgRate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
        json.put("type", type);
        json.put("description", description);
        json.put("avgRate", avgRate);
        json.put("imageUrl", imageUrl);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("deleted", deleted);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Restaurant create(Map<String, Object> json) {
        int id = (int) json.get("id");
        String name = (String) json.get("name");
        String type = (String) json.get("type");
        String description = (String)json.get("description");
        Double avgRate = (Double)json.get("avgRate");
        String imageUrl = (String)json.get("imageUrl");
        Double latitude = (Double) json.get("latitude");
        Double longitude = (Double)json.get("longitude");
        boolean deleted = (boolean)json.get("deleted");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        Restaurant restaurant = new Restaurant(name, type, description, avgRate, latitude, longitude);
        restaurant.setId(id);
        restaurant.setImageUrl(imageUrl);
        restaurant.setDeleted(deleted);
        restaurant.setUpdateDate(updateDate);
        return restaurant;
    }
}
