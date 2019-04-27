package com.example.splashit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.splashit.R;
import com.example.splashit.utils.Constants;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.PHOTO_ID)) {
                String id = getIntent().getStringExtra(Constants.PHOTO_ID);
                getSupportFragmentManager().beginTransaction().add(R.id.container, PhotoDetailFragment.newInstance(id)).commit();
            }
        }
    }
}
