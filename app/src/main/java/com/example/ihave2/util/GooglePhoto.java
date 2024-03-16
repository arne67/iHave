package com.example.ihave2.util;

import static com.example.ihave2.util.Constants.GOOGLE_PHOTO_ALBUMID;
import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.content.SharedPreferences;


public class GooglePhoto {
    private static final String TAG = "GooglePhoto";
    private static SharedPreferences mSharedPreferences;

     public static String getAlbumId(){
        SharedPreferences sharedPreferences;
        mSharedPreferences = initiateSharedPreferences();
        return mSharedPreferences.getString(GOOGLE_PHOTO_ALBUMID, null);
    }

    public static void putAlbumId(String albumId){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(GOOGLE_PHOTO_ALBUMID, albumId).apply();
    }

}
