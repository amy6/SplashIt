package com.example.splashit.ui;

import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.database.PhotoDatabase;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;
import com.example.splashit.utils.AppExecutors;
import com.example.splashit.utils.Constants;
import com.example.splashit.utils.PhotoAppUtils;
import com.example.splashit.widget.PhotoAppWidget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = PhotoDetailActivity.class.getSimpleName();

    @BindView(R.id.image) ImageView photoImage;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.adView) AdView adView;

    private PhotoDetailViewModel viewModel;
    private MenuItem favoritesItem;
    private MenuItem wallpaperItem;
    private String photoId;
    private Photo photo;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ButterKnife.bind(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel.class);

        if (PhotoAppUtils.checkInternetConnection(this)) {
            displayToastMessage(R.string.wallpaper_load_failure);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.PHOTO_ID)) {
                photoId = getIntent().getStringExtra(Constants.PHOTO_ID);
                if (photoId != null && !TextUtils.isEmpty(photoId)) {
                    ApiClient.getClient().create(ApiService.class)
                            .getPhoto(photoId, BuildConfig.UNSPLASH_API_KEY)
                            .enqueue(new Callback<Photo>() {
                                @Override public void onResponse(Call<Photo> call, Response<Photo> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        photo = response.body();
                                        Picasso.with(PhotoDetailActivity.this)
                                                .load(Uri.parse(response.body().getUrls().getRaw()))
                                                .into(photoImage);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        favoritesItem.setEnabled(true);
                                        wallpaperItem.setEnabled(true);
                                    }
                                }

                                @Override public void onFailure(Call<Photo> call, Throwable t) {
                                    Log.e(TAG, "Call to get photo failed : " + t);
                                    displayToastMessage(R.string.wallpaper_load_failure);
                                }
                            });
                }
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_detail_menu, menu);
        favoritesItem = menu.findItem(R.id.favorite);
        wallpaperItem = menu.findItem(R.id.wallpaper);
        AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
            boolean isFavorite = viewModel.isFavorite(photoId);
            if (isFavorite) {
                photo = PhotoDatabase.getInstance(this).photoDao().getPhoto(photoId);
                runOnUiThread(() -> favoritesItem.setIcon(R.drawable.ic_favorite));
            } else {
                runOnUiThread(() -> favoritesItem.setIcon(R.drawable.ic_favorite_border));
            }
        });
        favoritesItem.setEnabled(false);
        wallpaperItem.setEnabled(false);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                if (!favoritesItem.isEnabled()) {
                    displayToastMessage(R.string.wallpaper_load_in_progress);
                    return false;
                }
                AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
                            boolean isFavorite = viewModel.isFavorite(photoId);
                            if (isFavorite) {
                                viewModel.removePhotoFromFavorites(photo);
                                runOnUiThread(() -> {
                                    displayToastMessage(R.string.favorites_removed);
                                    favoritesItem.setIcon(R.drawable.ic_favorite_border);
                                });
                            } else {
                                viewModel.addPhotoToFavorites(photo);
                                runOnUiThread(() -> {
                                    displayToastMessage(R.string.favorites_added);
                                    favoritesItem.setIcon(R.drawable.ic_favorite);
                                });
                            }
                            viewModel.updatePhotoFavorite(photoId, !isFavorite);
                            updateWidget();
                        }
                );
                return true;
            case R.id.wallpaper:
                if (!wallpaperItem.isEnabled()) {
                    displayToastMessage(R.string.wallpaper_load_in_progress);
                    return false;
                }
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                InputStream inputStream;
                try {
                    inputStream = new URL(photo.getUrls().getRaw()).openStream();
                    wallpaperManager.setStream(inputStream);
                    displayToastMessage(R.string.wallpaper_update_success);
                } catch (Exception e) {
                    displayToastMessage(R.string.wallpaper_update_failure);
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PhotoAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
    }

    private void displayToastMessage(int messageId) {
        cancelToast();

        toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
