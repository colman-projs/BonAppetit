package com.example.BonAppetit.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {

    @Query("select * from Review where id = :id")
    Review findById(int id);

    @Query("select * from Review")
    List<Review> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Review... reviews);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Review review);

    @Query("select id from Review where ROWID = :rowid")
    int getIdByRowid(long rowid);

    @Delete
    void delete(Review review);
}
