package com.example.splashit.data.network;

import com.example.splashit.data.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("photos")
    Call<List<Photo>> getPhotos(@Query("client_id") String apiKey);

    @GET("photos/{id}")
    Call<Photo> getPhoto(@Path("id") String id, @Query("client_id") String apiKey);
}
