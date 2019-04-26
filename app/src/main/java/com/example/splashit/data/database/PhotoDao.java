package com.example.splashit.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.splashit.data.model.Photo;

import java.util.List;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photo")
    LiveData<List<Photo>> getFavoritesPhotos();

    @Query("SELECT is_favorite FROM photo WHERE photo_id = :photoId")
    boolean isFavorite(int photoId);

    @Query("UPDATE photo SET is_favorite = :isFavorite WHERE photo_id = :photoId" )
    void updatePhotoFavorite(int photoId, boolean isFavorite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPhotoToFavorites(Photo photo);

    @Delete
    void removePhotoFromFavorites(Photo photo);

    @Query("SELECT * FROM photo WHERE photo_id = :photoId")
    Photo getPhoto(int photoId);
}
