package com.com.m4u.database;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDAO {
    @Insert(onConflict = IGNORE)
    void addFavorite(Favorite... favorites);

    @Query("DELETE FROM Favorite WHERE path = :path")
    void deleteFavorite(String path);

    @Query("SELECT * FROM Favorite")
    List<Favorite> getAllFavorites();

}
