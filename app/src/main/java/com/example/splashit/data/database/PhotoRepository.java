package com.example.splashit.data.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.example.splashit.data.model.Photo;
import com.example.splashit.utils.AppExecutors;

import java.util.List;

public class PhotoRepository {

    private PhotoDao photoDao;
    private AppExecutors appExecutors;

    private LiveData<List<Photo>> photos;

    public PhotoRepository(Application application) {
        photoDao = PhotoDatabase.getInstance(application).photoDao();
        photos = photoDao.getFavoritesPhotos();

        appExecutors = AppExecutors.getExecutorInstance();
    }

    public LiveData<List<Photo>> getFavoritePhotos() {
        return photos;
    }

    public boolean isFavorite(String photoId) {
        return photoDao.isFavorite(photoId);
    }

    public void updatePhotoFavorite(String photoId, boolean isFavorite) {
        appExecutors.getDiskIO().execute(() -> {
            photoDao.updatePhotoFavorite(photoId, isFavorite);
        });
    }

    public void addPhotoToFavorites(Photo photo) {
        appExecutors.getDiskIO().execute(() -> {
            photoDao.addPhotoToFavorites(photo);
        });
    }

    public void removePhotoFromFavorites(Photo photo) {
        appExecutors.getDiskIO().execute(() -> {
            photoDao.removePhotoFromFavorites(photo);
        });
    }
}
