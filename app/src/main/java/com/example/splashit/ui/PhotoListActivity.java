package com.example.splashit.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;
import com.example.splashit.utils.Constants;
import com.example.splashit.utils.PhotoAppUtils;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = PhotoListActivity.class.getSimpleName();
    public static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.errorLayout)
    ScrollView errorLayout;
    @BindView(R.id.errorImage)
    ImageView errorImage;
    @BindView(R.id.errorText)
    TextView errorText;
    @BindView(R.id.errorButton)
    Button errorButton;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private List<Photo> photos;
    private PhotoRecyclerViewAdapter photoAdapter;
    private GridLayoutManager layoutManager;
    private Parcelable recyclerViewState;
    private PhotoListViewModel viewModel;
    private boolean favoritesClicked;
    private boolean fromErrorButton;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        ButterKnife.bind(this);

        MobileAds.initialize(this, ADMOB_APP_ID);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));

        errorButton.setOnClickListener(this);

        photos = new ArrayList<>();
        photoAdapter = new PhotoRecyclerViewAdapter(this, photos);
        PhotoAppUtils.setupRecyclerView(this, recyclerView);
        recyclerView.setAdapter(photoAdapter);
        layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER_STATE);
            layoutManager.onRestoreInstanceState(recyclerViewState);
            favoritesClicked = savedInstanceState.getBoolean(Constants.FAVORITES_CLICKED);
        }

        viewModel = ViewModelProviders.of(this).get(PhotoListViewModel.class);
        viewModel.getFavoritePhotos().observe(this, favorites -> {
            if (favorites != null && favorites.size() > 0 && favoritesClicked) {
                if (photos == null) {
                    photos = new ArrayList<>();
                } else {
                    photoAdapter.clear();
                }
                photoAdapter.addAll(favorites);
            } else if (favoritesClicked) {
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                        R.drawable.ic_error_outline, R.string.browse_photos);
            }
        });

        if (!favoritesClicked) {
            getPhotosList();
        }
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
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            photoAdapter.addAll(favoritePhotos);
        } else {
            updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                    R.drawable.ic_error_outline, R.string.browse_photos);
        }
    }

    private void getPhotosList() {
        favoritesClicked = false;
        if (PhotoAppUtils.checkInternetConnection(this)) {
            updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection, R.drawable.ic_cloud_off, R.string.error_try_again);
            return;
        }
        photoAdapter.clear();
        ApiClient.getClient().create(ApiService.class)
                .getPhotos(BuildConfig.UNSPLASH_API_KEY).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.body() == null || response.body().size() == 0) {
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_photo_library, R.string.error_no_results);
                    return;
                }
                photoAdapter.addAll(response.body());
                Log.i(TAG, "Photos retrieved " + photos.size());
                refreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (recyclerViewState != null) {
                    layoutManager.onRestoreInstanceState(recyclerViewState);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.i(TAG, "Call to get photos failed  : " + t);
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.error_no_results);
            }
        });
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
        outState.putBoolean(Constants.FAVORITES_CLICKED, favoritesClicked);
    }

    private void updateEmptyStateViews(int errorImage, int errorText, int errorTextDrawable, int errorButtonText) {

        errorLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        this.errorImage.setImageResource(errorImage);
        this.errorText.setText(errorText);
        this.errorText.setCompoundDrawablesWithIntrinsicBounds(0, errorTextDrawable, 0, 0);
        errorButton.setText(errorButtonText);
    }

    @Override public void onRefresh() {
        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        if (!fromErrorButton) {
            progressBar.setVisibility(View.GONE);
        } else {
            displayToast(getString(R.string.trying_again_alert));
            fromErrorButton = false;
        }

        getPhotosList();
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.errorButton:
                if (((Button) v).getText().toString().trim().equalsIgnoreCase(getString(R.string.error_try_again))) {
                    progressBar.setVisibility(View.VISIBLE);
                    fromErrorButton = true;
                    onRefresh();
                } else if (((Button) v).getText().toString().trim().equalsIgnoreCase(getString(R.string.browse_photos))) {
                    errorLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    getPhotosList();
                } else {
                    finish();
                }
                break;
        }
    }

    private void displayToast(String message) {
        cancelToast();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelToast();
    }
}
