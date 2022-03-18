package com.example.BonAppetit.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.BonAppetit.MyApplication;

@Database(entities = {Student.class, User.class}, version = 5)
//@Database(entities = {User.class}, version = 6)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract  UserDao userDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

