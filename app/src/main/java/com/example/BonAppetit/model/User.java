package com.example.BonAppetit.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class User {
    final public static String COLLECTION_NAME = "users";

    @PrimaryKey
    @NonNull
    String id = "";
    String mail = "";
    String password = "";
    String firstName = "";
    String lastName = "";
    String avatarUrl;
    boolean deleted = false;
    Long updateDate = new Long(0);

    public User(){}
    public User(String mail, String password, String firstName, String lastName) {
        this.mail = mail;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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
        json.put("mail", mail);
        json.put("password", password);
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("avatarUrl", avatarUrl);
        json.put("deleted", deleted);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static User create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String mail = (String) json.get("mail");
        String password = (String) json.get("password");
        String firstName = (String)json.get("firstName");
        String lastName = (String)json.get("lastName");
        String avatarUrl = (String)json.get("avatarUrl");
        boolean deleted = (boolean)json.get("deleted");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        User user = new User(mail, password, firstName, lastName);
        user.setId(id);
        user.setAvatarUrl(avatarUrl);
        user.setDeleted(deleted);
        user.setUpdateDate(updateDate);
        return user;
    }
}

