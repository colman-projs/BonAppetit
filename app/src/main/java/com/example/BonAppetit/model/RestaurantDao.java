package com.example.BonAppetit.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDao {

    @Query("select * from Restaurant where id = :id")
    Restaurant findById(int id);

    @Query("select * from Restaurant")
    List<Restaurant> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Restaurant... restaurants);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Restaurant restaurant);

    @Query("select id from Restaurant where ROWID = :rowid")
    String getIdByRowid(long rowid);

    @Delete
    void delete(Restaurant restaurant);
}
