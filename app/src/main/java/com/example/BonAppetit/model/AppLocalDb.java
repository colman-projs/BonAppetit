package com.example.BonAppetit.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.BonAppetit.MyApplication;

@Database(entities = {User.class, Restaurant.class}, version = 6)
abstract class AppLocalDbRepository extends RoomDatabase {
//    public abstract StudentDao studentDao();
    public abstract UserDao userDao();
    public abstract RestaurantDao restaurantDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

