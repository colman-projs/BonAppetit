package com.example.BonAppetit.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.BonAppetit.MyApplication;
import com.example.BonAppetit.dao.RestaurantDao;
import com.example.BonAppetit.dao.RestaurantTypeDao;
import com.example.BonAppetit.dao.ReviewDao;
import com.example.BonAppetit.dao.UserDao;

@Database(entities = {User.class, Restaurant.class, RestaurantType.class, Review.class}, version = 18)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract RestaurantDao restaurantDao();
    public abstract RestaurantTypeDao restaurantTypeDao();
    public abstract ReviewDao reviewDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

