package com.com.m4u.view;

import android.app.Application;

import androidx.room.Room;

import com.com.m4u.database.SongDB;

public class App extends Application {
    private static App instance;
    private SongDB db;
    private Storage storage;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(this, SongDB.class, "songdb").build();
        storage = new Storage();
    }

    public SongDB getDb() {
        return db;
    }

    public Storage getStorage() {
        return storage;
    }
}
