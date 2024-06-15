package com.susarne.ihave2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Response;


public class GooglePhoto {
    private static final String TAG = "GooglePhoto";
    private static SharedPreferences mSharedPreferences;
    private static final String APP_TAG = "MyCustomApp";


    public static void createPhotoDir(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        } else {
            Log.d(TAG, "createPhotoDir: directory findes allerede");
        }
    }

}
