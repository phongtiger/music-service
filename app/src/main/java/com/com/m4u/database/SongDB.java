package com.com.m4u.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Favorite.class}, version = 1)
public abstract class SongDB extends RoomDatabase {
    public abstract SongDAO getSongDAO();
}
