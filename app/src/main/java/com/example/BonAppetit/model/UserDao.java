package com.example.BonAppetit.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from User where mail = :mail and password = :password")
    User findByLogin(String mail, String password);

    @Query("select * from User where id = :id")
    User getUserById(String id);

    @Query("select * from User")
    List<User> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Query("select id from User where ROWID = :rowid")
    String getIdByRowid(long rowid);

    @Delete
    void delete(User user);
}
