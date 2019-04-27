package com.example.splashit.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.splashit.R;
import com.example.splashit.data.database.PhotoDatabase;
import com.example.splashit.data.model.Photo;

import java.util.ArrayList;
import java.util.List;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = GridRemoteViewsFactory.class.getSimpleName();

    private Context context;
    private List<Photo> favoritesPhotos;
    private PhotoDatabase db;

    public GridRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {
        favoritesPhotos = new ArrayList<>();
        db = PhotoDatabase.getInstance(context);
    }

    @Override
    public void onDataSetChanged() {
        List<Photo> photoList = db.photoDao().getFavoritesPhotosList();
        if (photoList != null) {
            favoritesPhotos.addAll(photoList);
        }
    }

    @Override
    public void onDestroy() {
        db.close();
    }

    @Override
    public int getCount() {
        if (favoritesPhotos == null) return 0;
        return favoritesPhotos.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.photo_app_widget_list);
        Log.i(TAG, "Favorites photos : " + favoritesPhotos.size() + " " + favoritesPhotos.get(position).getUrls().getThumb());
        views.setImageViewUri(R.id.widget_photo_image, Uri.parse(favoritesPhotos.get(position).getUrls().getThumb()));
//        views.setImageViewResource(R.id.widget_photo_image, R.drawable.ic_favorite);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

