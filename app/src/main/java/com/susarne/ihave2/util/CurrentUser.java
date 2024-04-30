package com.susarne.ihave2.util;

import static com.susarne.ihave2.util.Constants.GOOGLE_PHOTO_ALBUMID;
import static com.susarne.ihave2.util.Constants.GOOGLE_PHOTO_ALBUMID_UPLOADED;
import static com.susarne.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.content.SharedPreferences;
import android.util.Log;

public class CurrentUser {
    private static final String TAG = "currentUser";
    private static SharedPreferences mSharedPreferences;
    private static String mMasterKeyAlias;


    //Hjælprutiner til Ihave tokens

    public static String getAccessToken(){
        SharedPreferences sharedPreferences;
        mSharedPreferences=initiateSharedPreferences();
        return mSharedPreferences.getString(Constants.IHAVE_ACCESS_TOKEN, null);
    }

    public static void putAccessToken(String accessToken){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(Constants.IHAVE_ACCESS_TOKEN, accessToken).apply();
        if (mSharedPreferences.getString(Constants.IHAVE_ACCESS_TOKEN, null)!=null){
            Log.d(TAG, "putAccessToken: getstring: "+mSharedPreferences.getString(Constants.IHAVE_ACCESS_TOKEN, null));
        }
    }

    public static void deleteAccessToken() {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().remove(Constants.IHAVE_ACCESS_TOKEN).apply();
    }

    public static int getUserId(){
        SharedPreferences sharedPreferences;
        mSharedPreferences=initiateSharedPreferences();
        return mSharedPreferences.getInt(Constants.IHAVE_USER_ID,0);
    }

    public static void putUserId(int userId){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putInt(Constants.IHAVE_USER_ID, userId).apply();
    }


    public static void deleteUserId() {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().remove(Constants.IHAVE_USER_ID).apply();
    }

    public static String getPhotoAlbumId(){
        SharedPreferences sharedPreferences;
        mSharedPreferences = initiateSharedPreferences();
        return mSharedPreferences.getString(GOOGLE_PHOTO_ALBUMID, null);
    }

    public static void putPhotoAlbumId(String albumId){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(GOOGLE_PHOTO_ALBUMID, albumId).apply();
    }

    public static void deletePhotoAlbumId() {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().remove(GOOGLE_PHOTO_ALBUMID).apply();
    }


    public static void putPhotoAlbumIdUploaded(boolean uploaded){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putBoolean(GOOGLE_PHOTO_ALBUMID_UPLOADED, uploaded).apply();
    }


    public static boolean isPhotoAlbumIdUploaded(){
        mSharedPreferences=initiateSharedPreferences();
        return mSharedPreferences.getBoolean(GOOGLE_PHOTO_ALBUMID_UPLOADED,false);
    }

    public static void deletePhotoAlbumIdUploaded() {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().remove(GOOGLE_PHOTO_ALBUMID_UPLOADED).apply();
    }


}
