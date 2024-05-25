package com.susarne.ihave2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.susarne.ihave2.api.GooglePhotoApiClient;
import com.susarne.ihave2.models.GooglePhotos.MediaItem;

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

    public static MediaItem getMediaItem(String mediaItemId) {
        String bearerToken = "Bearer " + CurrentUser.getAccessToken();
        //String mediaItemId = "APmtqhpX3H_hTbmP1FPNkXEONKRBE8pLCnXKCM5BjvFAvUSupr26yrKYdnLEB5x7Lgs7W9sQic9_g_w6WVqhoDceKq5dzULYow";
        Call<MediaItem> call = GooglePhotoApiClient.getInstance().getMyApi().getMediaItem(bearerToken, mediaItemId);
        try {
            Response<MediaItem> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                return response.body();
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }

        return null;
    }

    public static void downloadImage(String imageUrl, String photoFileName, Context context) {
//        Runnable runnable = new Runnable() {
//            public void run() {
                //some code here
                //String imageUrl = "https://lh3.googleusercontent.com/lr/AGiIYOXNckJeWODN7fkRrbd5CzS5TWGmALz4wAYgIK4itKpVmIu_rzeXWkk_ayynO4jtgi43VaRF1HFNfGnfs3bApCTkwkPz8smEhJQmKZWpHrsGn--0vrHnY2FM1RoXWM_2sMkI-8F-JuX3d2Yfl27oKSrbJwcANVMHO4XEmqXyvPfgTmll4nrL_W44eFCY1s8PcePy1SbnRmPSBlU5wBJ6M7cRVZae2eBZ3VV2WMSjivwsHUYqNHlYUc1JZfPVFadRy1xWMFc5CR-Azaw2ta8myHurcosqACumdtMaCUlPcyxCENsLOIqiUTUrV4dmbZ5fKqG5aSIzigB27wfcwZedYDSH32vsVsxX4e4rBzLD534AiHKKEtHDYQ-xmEfXDz5ZZwsvSBbovBNTXNRIBBcbwelEJKCXcOssGqwPiYHvpYMweC9FYoEjMJTn80hF2jfj0pNymITfYHl-hmf-ka0px9si9EiKQ2p0PjEfWqhCYTBkMhwVZ_yC3cLs3I3indkADp0fQ6rg4cIZacSGGVQUpO2fRXncWTNA_1cJkvVK7Q0k-dk3vGqh2rvla37qxSJGSuPCNjgu2ftm2UFy49NiLPL-Qmh_n8HNLzPnZtdqEiMOzWnm1XXRVrjwcn2sEtOhacWwpmnE5svM9s9pR3cwNZZFe-nBBcByBgKfSyiaUD4xuIG5c7dGS8Hc5zkPe8uYmKDMgmVUteH-PWwo2HqfKdQpt-xlaYI_u5gA238GwOJD8bRMJqMHX9dqAJherIWqTvr7O7etzxmmsjykhX0Y-hzD4OAulQyjUAJ7IAIm3msz0wxDrDQpsC65Fdk9v6OZIaIfWGgugFpBWkMOWmdqLbeJ05bESr6hzXFh80UU5xsqAFrFasgAJJdVD86uJnfAIuC8ahnIJMY3gohXpEMr38P3u-S9Pp6Q4dmaz7pFLjGlCiXJ3TsLID_d9KKWBaCvmA49uD0qtDvKZFYC1w";
                Bitmap bitmap;
                try {
                    // Download Image from URL
                    InputStream input = new java.net.URL(imageUrl+"=w3000-h4000").openStream();
                    // Decode Bitmap
                    bitmap = BitmapFactory.decodeStream(input);
                    saveFile(bitmap, photoFileName, context);
                } catch (Exception e) {
                    Log.d(TAG, "run: fejl");
                    e.printStackTrace();
                }
  //          }
  //      };
  //      Thread thread = new Thread(runnable);
  //      thread.start();
    }

    private static void saveFile(Bitmap bitmap, String photoFileName, Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        File smallImageFile = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        Log.d(TAG, "saveSmallFile: getpath: "+mediaStorageDir.getPath());
        Log.d(TAG, "saveSmallFile: getfilename: "+photoFileName);
        Log.d(TAG, "saveSmallFile: absolutPath: "+mediaStorageDir.getAbsolutePath());
        //Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 384, 512, false);
        try {/*from w  w  w.  j a v  a 2 s  .c  om*/
            smallImageFile.createNewFile();
            FileOutputStream out = new FileOutputStream(smallImageFile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createPhotoDir(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        } else {
            Log.d(TAG, "createPhotoDir: directory findes allerede");
        }
    }

}
