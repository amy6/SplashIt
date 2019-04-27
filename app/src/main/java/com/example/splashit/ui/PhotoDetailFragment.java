package com.example.splashit.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.splashit.BuildConfig;
import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.data.network.ApiClient;
import com.example.splashit.data.network.ApiService;
import com.example.splashit.utils.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoDetailFragment extends Fragment {

    public static final String TAG = PhotoDetailFragment.class.getSimpleName();

    @BindView(R.id.image) ImageView photoImage;
    @BindView(R.id.photographer) TextView photographer;
    @BindView(R.id.title) TextView title;

    public static PhotoDetailFragment newInstance(String id) {
        PhotoDetailFragment photoDetailFragment = new PhotoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PHOTO_ID, id);
        photoDetailFragment.setArguments(bundle);
        return photoDetailFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_detail_fragment, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            String id = getArguments().getString(Constants.PHOTO_ID);
            if (id != null && !TextUtils.isEmpty(id)) {
                ApiClient.getClient().create(ApiService.class)
                        .getPhoto(id, BuildConfig.UNSPLASH_API_KEY)
                        .enqueue(new Callback<Photo>() {
                            @Override public void onResponse(Call<Photo> call, Response<Photo> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Picasso.with(getContext())
                                            .load(Uri.parse(response.body().getUrls().getRaw()))
                                            .into(photoImage);
                                }
                            }

                            @Override public void onFailure(Call<Photo> call, Throwable t) {
                                Log.e(TAG, "Call to get photo failed : " + t);
                            }
                        });
            }
        }
    }

}
