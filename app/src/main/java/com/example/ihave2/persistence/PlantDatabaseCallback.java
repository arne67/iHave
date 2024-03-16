package com.example.ihave2.persistence;

import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class PlantDatabaseCallback extends RoomDatabase.Callback {
    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        super.onCreate(db);
        // Udfører handlinger efter oprettelse af databasen
    }

    @Override
    public void onOpen(SupportSQLiteDatabase db) {
        super.onOpen(db);
        // Udfører handlinger når databasen åbnes
    }
}

