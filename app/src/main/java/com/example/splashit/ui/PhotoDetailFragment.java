package com.example.splashit.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.utils.Constants;

public class PhotoDetailFragment extends Fragment {

    private Photo photo;
    private PhotoDetailViewModel mViewModel;

    public static PhotoDetailFragment newInstance(String id) {
        PhotoDetailFragment photoDetailFragment = new PhotoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PHOTO_ID, id);
        photoDetailFragment.setArguments(bundle);
        return new PhotoDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel.class);
        if (getArguments() != null) {
            String id = getArguments().getString(Constants.PHOTO_ID);
            if (id != null && !TextUtils.isEmpty(id)) {
                photo = mViewModel.getPhoto(id);
            }
        }

    }

}
