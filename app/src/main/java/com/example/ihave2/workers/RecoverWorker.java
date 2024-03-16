package com.example.ihave2.workers;


import static com.example.ihave2.util.GoogleDrive.downloadFile;
import static com.example.ihave2.util.GoogleDrive.getFileId;
import static com.example.ihave2.util.GoogleDrive.getFolderId;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


public class RecoverWorker extends Worker {
    private static final String TAG = "RecoverWorker";
    private static final String APP_TAG = "MyCustomApp";

    private Context context;
    private SQLiteDatabase database;
    private String mFolderName;


    public RecoverWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        // Åbn SQLite-databasen
        database = context.openOrCreateDatabase("plants_db", Context.MODE_PRIVATE, null);
        mFolderName = "2024-02-26T22:19:22.286850";
        String folderId = getFolderId(mFolderName);
        recover(folderId, "plants");


        database.close();

        //String folderId = createDriveFolder(null,"klokken10",context);

        return Result.success();
    }

    public String recover(String folderId, String tableName) {
        String fileId = getFileId(folderId, tableName + ".json");
        OutputStream outputStream = downloadFile(fileId);
        JSONArray jsonArray = convertOutputStreamToJSONArray(outputStream);
        Log.d(TAG, "recover: jsonarray" + jsonArray.toString());
        for (int i = 0; i < jsonArray.length(); i++) {

            // store each object in JSONObject
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // get field value from JSONObject using get() method
            try {
                if (jsonObject.get("plantId").equals("0")) {
                    Log.d(TAG, "recover: vi fandt plantId 0");
                    jsonObject.put("plantId", "100");
                    insertJsonDataIntoTable("plants", jsonObject);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return "x";
    }


    public static JSONArray convertOutputStreamToJSONArray(OutputStream outputStream) {
        // Konverterer outputStream til en streng
        String jsonString = convertOutputStreamToString(outputStream);
        JSONArray jsonArray;

        // Opretter et JSONArray ud fra strengen
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonArray;

    }

    private static String convertOutputStreamToString(OutputStream outputStream) {
        if (outputStream instanceof ByteArrayOutputStream) {
            try {
                return ((ByteArrayOutputStream) outputStream).toString(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Hvis outputStream ikke er en ByteArrayOutputStream, kaster vi en exception
            throw new IllegalArgumentException("Output stream is not of type ByteArrayOutputStream");
        }
    }

    private String backup(String tableName) {

        // Hent data fra tabellen
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        // Omdan data til JSON
        JSONArray jsonArray = cursorToJsonArray(cursor);

        // Luk cursor og database
        cursor.close();

        // Skriv JSON til fil
        return writeJsonToFile(jsonArray, tableName + ".json");
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
            String localUri = Environment.DIRECTORY_DOCUMENTS + '/' + mFolderName;
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

    private void insertJsonDataIntoTable(String tableName, JSONObject jsonObject) {
        Log.d(TAG, "insertJsonDataIntoTable: "+tableName+"/"+jsonObject.toString());
        try {
            // Slet eksisterende data fra tabellen
            //database.execSQL("DELETE FROM " + tableName);

            ContentValues contentValues = new ContentValues();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                contentValues.put(key, value);
            }
            database.insert(tableName, null, contentValues);

            Log.d("JsonToSQLiteConverter", "JSON-data er indsat i tabel: " + tableName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
