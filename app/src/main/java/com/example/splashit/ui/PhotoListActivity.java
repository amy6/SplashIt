package com.example.splashit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.utils.Constants;

public class PhotoListActivity extends AppCompatActivity implements PhotoFragment.OnListFragmentInteractionListener {

    private static final String TAG = PhotoListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.container,
                PhotoFragment.newInstance(2)).commit();
    }

    @Override
    public void onListFragmentInteraction(Photo item) {
        Log.i(TAG, "Item clicked : " + item.getId());
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(Constants.PHOTO_ID, item.getId());
        startActivity(intent);
    }
}
