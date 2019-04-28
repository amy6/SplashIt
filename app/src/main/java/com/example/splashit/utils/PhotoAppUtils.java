package com.example.splashit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class PhotoAppUtils {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo == null || !networkInfo.isConnectedOrConnecting();
    }

    public static void setupRecyclerView(Context context, RecyclerView recyclerView) {

        recyclerView.setLayoutManager(new GridLayoutManager(context, getSpanCount(context)));
        recyclerView.setItemViewCacheSize(Constants.ITEM_VIEW_CACHE_SIZE);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    private static int getSpanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}
