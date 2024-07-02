package com.susarne.ihave2.util;

import android.content.Context;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.susarne.ihave2.app.MyApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferences {
    private static android.content.SharedPreferences mSharedPreferences;
    private static final String TAG = "SharedPreferences";



    public static android.content.SharedPreferences initiateSharedPreferences(){
        Context context;
        context= MyApplication.getAppContext();
        try {
            return EncryptedSharedPreferences.create(
                    "secret_shared_prefs2",
                    getMasterKey(),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            Log.d(TAG, "initiateSharedPreferences: fejl1");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d(TAG, "initiateSharedPreferences: fejl2");
            e.printStackTrace();
            return null;
        }

    }
    public static String getMasterKey() {
        try {
            return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException e) {
            Log.d(TAG, "getMasterKey: fejl1");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d(TAG, "getMasterKey: fejl2");
            e.printStackTrace();
            return null;
        }
    }

}

