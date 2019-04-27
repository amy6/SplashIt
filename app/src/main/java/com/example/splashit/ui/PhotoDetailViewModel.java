package com.example.splashit.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.splashit.data.database.PhotoRepository;
import com.example.splashit.data.model.Photo;

public class PhotoDetailViewModel extends AndroidViewModel {

    private PhotoRepository photoRepository;

    public PhotoDetailViewModel(@NonNull Application application) {
        super(application);
        photoRepository = new PhotoRepository(application);
    }

    public boolean isFavorite(String photoId) {
        return photoRepository.isFavorite(photoId);
    }

    public void updatePhotoFavorite(String photoId, boolean isFavorite) {
        photoRepository.updatePhotoFavorite(photoId, isFavorite);
    }

    public void addPhotoToFavorites(Photo photo) {
        photoRepository.addPhotoToFavorites(photo);
    }

    public void removePhotoFromFavorites(Photo photo) {
        photoRepository.removePhotoFromFavorites(photo);
    }
}
