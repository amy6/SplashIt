package com.example.splashit.utils;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

public class PhotoAppUtils {

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
