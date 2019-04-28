package com.example.splashit.widget;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.splashit.data.database.PhotoDatabase;
import com.example.splashit.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PhotoWallpaperService extends IntentService {

    private static final String TAG = PhotoWallpaperService.class.getSimpleName();

    public PhotoWallpaperService() {
        super("PhotoWallpaperService");
    }

    @Override protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(Constants.PHOTO_ID)) {
                String photoId = intent.getStringExtra(Constants.PHOTO_ID);
                Log.i(TAG, "Photo ID : " + photoId);
                String url = PhotoDatabase.getInstance(this).photoDao().getPhoto(photoId).getUrls().getRegular();
                updateWallpaper(url);
            }
        }
    }

    private void updateWallpaper(String url) {
        if (url != null && !TextUtils.isEmpty(url)) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            Log.i(TAG, "Setting wallpaper with photo ID : " + url);
            InputStream inputStream;
            try {
                inputStream = new URL(url).openStream();
                wallpaperManager.setStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
