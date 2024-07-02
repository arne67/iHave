package com.susarne.ihave2.persistence;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.System;
import com.susarne.ihave2.models.Taxon;

import java.util.concurrent.Executors;

//nytfelt - version
@Database(entities = {Plant.class, PlantPhoto.class, System.class, Taxon.class},version = 18)

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
                    .fallbackToDestructiveMigrationFrom(8,9,10,11,12,13,14,15,16)
                    .addMigrations(MIGRATION_17_18)
                    //.setQueryCallback()
                    .setQueryCallback(((sqlQuery, bindArgs) ->
                            Log.d(TAG,"SQL QUERY: " + sqlQuery + ".... Args: " + bindArgs)), Executors.newSingleThreadExecutor())
                    .addCallback(new PlantDatabaseCallback())
                    .build();
        }
        return instance;

    }
    public abstract PlantDao getPlantDao();

    static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we are only adding a table, there's no need to drop anything
            database.execSQL("CREATE TABLE IF NOT EXISTS `Taxon` (`arterId` TEXT NOT NULL, `taxonRang` TEXT, `videnskabeligtNavn` TEXT, `danskNavn` TEXT, `danskSynonym` TEXT, `artsGruppe` TEXT, `beskrivelse` TEXT, `taxonId` TEXT, `parentTaxonid` TEXT, PRIMARY KEY(`arterId`))");
            database.execSQL("ALTER TABLE System ADD COLUMN lastGetUpdatedTaxonsUntil TEXT;");

        }
    };
}
