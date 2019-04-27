package com.example.splashit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoListActivity extends AppCompatActivity {

    private static final String TAG = PhotoListActivity.class.getSimpleName();

    private ArrayList<Photo> photos;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private PhotoRecyclerViewAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        ButterKnife.bind(this);

        photos = new ArrayList<>();
        photoAdapter = new PhotoRecyclerViewAdapter(this, photos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(photoAdapter);

        ApiClient.getClient().create(ApiService.class)
                .getPhotos(BuildConfig.UNSPLASH_API_KEY).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.body() != null) {
                    photos.addAll(response.body());
                    Log.i(TAG, "Photos retrieved " + photos.size());
                    photoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.i(TAG, "Call to get photos failed  : " + t);
            }
        });
    }
}
