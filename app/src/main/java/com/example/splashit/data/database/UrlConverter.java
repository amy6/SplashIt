package com.example.splashit.data.database;

import android.arch.persistence.room.TypeConverter;

import com.example.splashit.data.model.Urls;
import com.google.gson.Gson;

public class UrlConverter {

    @TypeConverter
    public static String fromJson(Urls urls) {
        return urls == null ? null : new Gson().toJson(urls);
    }

    @TypeConverter
    public static Urls jsonToUrls(String json) {
        return json == null ? null : new Gson().fromJson(json, Urls.class);
    }
}
