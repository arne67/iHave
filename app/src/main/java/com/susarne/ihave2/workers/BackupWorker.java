package com.susarne.ihave2.workers;


import static com.susarne.ihave2.util.GoogleDrive.createDriveFile;
import static com.susarne.ihave2.util.GoogleDrive.createDriveFolder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.susarne.ihave2.persistence.PlantRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public class BackupWorker extends Worker {
    private static final String TAG = "BackupWorker";
    private static final String APP_TAG = "MyCustomApp";

    private Context context;
    private SQLiteDatabase database;
    private String mFolderName;


    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;

    }

    @NonNull
    @Override
    public Result doWork() {
        // Åbn SQLite-databasen
        database = context.openOrCreateDatabase("plants_db", Context.MODE_PRIVATE, null);
        LocalDateTime currentTimestamp = LocalDateTime.now();
        mFolderName =currentTimestamp.toString();
        String folderId = createDriveFolder(null, mFolderName,context);
        PlantRepository mPlantRepository = new PlantRepository(context);
        List<String> tableNames = mPlantRepository.getAllTableNames();
        for (String tableName:tableNames) {
            Log.d(TAG, "doWork: tabelnavn: "+tableName);
            String localUri=backup(tableName);
            String fileName=tableName+".json";
            createDriveFile(folderId,fileName,localUri,context);
        }


        database.close();

        //String folderId = createDriveFolder(null,"klokken10",context);

        return Result.success();
    }

    private String backup(String tableName) {

        // Hent data fra tabellen
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        // Omdan data til JSON
        JSONArray jsonArray = cursorToJsonArray(cursor);

        // Luk cursor og database
        cursor.close();

        // Skriv JSON til fil
        return writeJsonToFile(jsonArray, tableName+".json");
    }


    private JSONArray cursorToJsonArray(Cursor cursor) {
        JSONArray jsonArray = new JSONArray();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                int columnsCount = cursor.getColumnCount();
                for (int i = 0; i < columnsCount; i++) {
                    try {
                        String columnName = cursor.getColumnName(i);
                        String columnValue = cursor.getString(i);
                        jsonObject.put(columnName, columnValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
        }

        return jsonArray;
    }

    private String writeJsonToFile(JSONArray jsonArray, String fileName) {
        try {
            // Opret en JSON-fil på den eksterne lagerenhed
            String localUri=Environment.DIRECTORY_DOCUMENTS+'/'+ mFolderName;
            File file = new File(context.getExternalFilesDir(localUri), fileName);

            // Skriv JSON til filen
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonArray.toString());
            fileWriter.flush();
            fileWriter.close();
            Log.d("SQLiteToJsonConverter", "JSON data skrevet til fil: " + file.getAbsolutePath());
            return localUri;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
