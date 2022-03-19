package com.example.BonAppetit.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.BonAppetit.MyApplication;

@Database(entities = {User.class, Restaurant.class, RestaurantType.class}, version = 7)
abstract class AppLocalDbRepository extends RoomDatabase {
//    public abstract StudentDao studentDao();
    public abstract UserDao userDao();
    public abstract RestaurantDao restaurantDao();
    public abstract RestaurantTypeDao restaurantTypeDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

