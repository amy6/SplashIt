package com.example.splashit.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;
import com.example.splashit.utils.Constants;
import com.example.splashit.utils.PhotoAppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoListActivity extends AppCompatActivity {

    private static final String TAG = PhotoListActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<Photo> photos;
    private PhotoRecyclerViewAdapter photoAdapter;
    private GridLayoutManager layoutManager;
    private Parcelable recyclerViewState;
    private PhotoListViewModel viewModel;
    private boolean favoritesClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        ButterKnife.bind(this);

        photos = new ArrayList<>();
        photoAdapter = new PhotoRecyclerViewAdapter(this, photos);
        PhotoAppUtils.setupRecyclerView(this, recyclerView);
        recyclerView.setAdapter(photoAdapter);
        layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        viewModel = ViewModelProviders.of(this).get(PhotoListViewModel.class);
        viewModel.getFavoritePhotos().observe(this, favorites -> {
            if (favorites != null && favoritesClicked) {
                if (photos == null) {
                    photos = new ArrayList<>();
                } else {
                    photoAdapter.clear();
                }
                photoAdapter.addAll(favorites);
            }
        });

        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER_STATE);
            layoutManager.onRestoreInstanceState(recyclerViewState);
        }

        getPhotosList();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_list_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                if (favoritesClicked) {
                    return false;
                }
                getFavoritePhotos();
                return true;
            case R.id.list:
                if (!favoritesClicked) {
                    return false;
                }
                getPhotosList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavoritePhotos() {
        favoritesClicked = true;
        photoAdapter.clear();
        List<Photo> favoritePhotos = viewModel.getFavoritePhotos().getValue();
        if (favoritePhotos != null && favoritePhotos.size() > 0) {
            photoAdapter.addAll(favoritePhotos);
        }
    }

    private void getPhotosList() {
        favoritesClicked = false;
        photoAdapter.clear();
        ApiClient.getClient().create(ApiService.class)
                .getPhotos(BuildConfig.UNSPLASH_API_KEY).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.body() != null) {
                    photoAdapter.addAll(response.body());
                    Log.i(TAG, "Photos retrieved " + photos.size());
                    if (recyclerViewState != null) {
                        layoutManager.onRestoreInstanceState(recyclerViewState);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.i(TAG, "Call to get photos failed  : " + t);
            }
        });
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
    }
}
