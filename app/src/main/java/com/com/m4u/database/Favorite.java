package com.com.m4u.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "album")
    public String album;

    public Favorite(@NonNull String path, String title, String album) {
        this.path = path;
        this.title = title;
        this.album = album;
    }
}
