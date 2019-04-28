package com.example.splashit.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.splashit.R;
import com.example.splashit.data.database.PhotoDatabase;
import com.example.splashit.data.model.Photo;
import com.example.splashit.ui.PhotoDetailActivity;
import com.example.splashit.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            favoritesPhotos.clear();
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
        if (favoritesPhotos == null || favoritesPhotos.size() == 0) return null;

        Photo photo = favoritesPhotos.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.photo_app_widget_list);

        HttpURLConnection connection;
        try {
            URL url = new URL(photo.getUrls().getThumb());
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            views.setImageViewBitmap(R.id.widget_photo_image, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle extras = new Bundle();
        extras.putString(Constants.PHOTO_ID, photo.getId());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_photo_image, fillInIntent);

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

