package com.example.splashit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.splashit.R;
import com.example.splashit.ui.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements PhotoFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.container, PhotoFragment.newInstance(2)).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i(TAG, "Item clicked : " + item.id);
    }
}
