package com.example.splashit.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.splashit.data.model.Photo;

@Database(entities = {Photo.class}, version = 2, exportSchema = false)
@TypeConverters({UrlConverter.class})
public abstract class PhotoDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "photo";

    private static final Object LOCK = new Object();
    private static volatile PhotoDatabase photoDatabaseInstance;

    public static PhotoDatabase getInstance(Context context) {

        if (photoDatabaseInstance == null) {
            synchronized (LOCK) {
                if (photoDatabaseInstance == null) {
                    photoDatabaseInstance = Room.databaseBuilder(context, PhotoDatabase.class,
                            PhotoDatabase.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return photoDatabaseInstance;
    }

    public abstract PhotoDao photoDao();
}
