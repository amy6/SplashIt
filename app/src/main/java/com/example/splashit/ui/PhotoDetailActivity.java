package com.example.splashit.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.database.PhotoDatabase;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;
import com.example.splashit.utils.AppExecutors;
import com.example.splashit.utils.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = PhotoDetailActivity.class.getSimpleName();

    @BindView(R.id.image) ImageView photoImage;

    private PhotoDetailViewModel viewModel;
    private MenuItem favoritesItem;
    private String photoId;
    private Photo photo;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_fragment);

        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel.class);

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
                                    }
                                }

                                @Override public void onFailure(Call<Photo> call, Throwable t) {
                                    Log.e(TAG, "Call to get photo failed : " + t);
                                }
                            });
                }
//                getSupportFragmentManager().beginTransaction().add(R.id.container, PhotoDetailFragment.newInstance(photoId)).commit();
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_detail_menu, menu);
        favoritesItem = menu.findItem(R.id.favorite);
        AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
            boolean isFavorite = viewModel.isFavorite(photoId);
            if (isFavorite) {
                photo = PhotoDatabase.getInstance(this).photoDao().getPhoto(photoId);
                runOnUiThread(() -> favoritesItem.setIcon(R.drawable.ic_favorite));
            } else {
                runOnUiThread(() -> favoritesItem.setIcon(R.drawable.ic_favorite_border));
            }
        });
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
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
                        }
                );
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
