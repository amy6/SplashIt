package com.example.splashit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.splashit.ui.PhotoDetailActivity;
import com.example.splashit.utils.Constants;
import com.example.splashit.widget.GridWidgetService;
import com.example.splashit.widget.PhotoWallpaperService;

/**
 * Implementation of App Widget functionality.
 */
public class PhotoAppWidget extends AppWidgetProvider {

    public static final String TAG = PhotoAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.photo_app_widget);

        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        Intent appIntent = new Intent(context, PhotoWallpaperService.class);
        Log.i(TAG, "Photo ID : " + intent.getStringExtra(Constants.PHOTO_ID));
        PendingIntent appPendingIntent = PendingIntent.getService(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

