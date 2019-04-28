package com.example.splashit.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.splashit.data.database.PhotoRepository;
import com.example.splashit.data.model.Photo;

import java.util.List;

public class PhotoListViewModel extends AndroidViewModel {

    private LiveData<List<Photo>> photos;

    public PhotoListViewModel(@NonNull Application application) {
        super(application);
        PhotoRepository photoRepository = new PhotoRepository(application);
        photos = photoRepository.getFavoritePhotos();
    }

    public LiveData<List<Photo>> getFavoritePhotos() {
        return photos;
    }
}
