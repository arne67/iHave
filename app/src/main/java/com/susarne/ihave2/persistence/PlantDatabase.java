package com.susarne.ihave2.persistence;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.System;

import java.util.concurrent.Executors;

//nytfelt - version
@Database(entities = {Plant.class, PlantPhoto.class, System.class},version = 16)

public abstract class PlantDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "plants_db";
    private static final String TAG = "PlantDatabase";

    private static PlantDatabase instance;

    static PlantDatabase getInstance(final Context context){
        if (instance==null){
            instance= Room.databaseBuilder(
                    context.getApplicationContext(),
                    PlantDatabase.class,
                    DATABASE_NAME
            )
                    .fallbackToDestructiveMigrationFrom(8,9,10,11,12,13,14,15)
                    .addMigrations()
                    //.setQueryCallback()
                    .setQueryCallback(((sqlQuery, bindArgs) ->
                            Log.d(TAG,"SQL QUERY: " + sqlQuery + ".... Args: " + bindArgs)), Executors.newSingleThreadExecutor())
                    .addCallback(new PlantDatabaseCallback())
                    .build();
        }
        return instance;

    }
    public abstract PlantDao getPlantDao();
}
