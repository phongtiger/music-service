package com.com.m4u.view.model;

import android.graphics.Bitmap;

import com.com.m4u.database.Favorite;

import java.util.Objects;

public class Song {
    public String title, path, album, artist;
    public Bitmap photo;

    public Song(String title, String path, String album, String artist) {
        this.title = title;
        this.path = path;
        this.album = album;
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return path.equals(song.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public Favorite toFavorite() {
        return new Favorite(path, title + "," + artist, album);
    }
}
