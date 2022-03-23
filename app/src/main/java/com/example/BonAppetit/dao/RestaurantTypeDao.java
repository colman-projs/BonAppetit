package com.example.BonAppetit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.BonAppetit.model.RestaurantType;

import java.util.List;

@Dao
public interface RestaurantTypeDao {

    @Query("select * from RestaurantType where id = :id")
    RestaurantType findById(int id);

    @Query("select * from RestaurantType")
    List<RestaurantType> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RestaurantType... restaurantTypes);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(RestaurantType restaurantType);

    @Query("select id from RestaurantType where ROWID = :rowid")
    String getIdByRowid(long rowid);

    @Delete
    void delete(RestaurantType restaurantType);
}
