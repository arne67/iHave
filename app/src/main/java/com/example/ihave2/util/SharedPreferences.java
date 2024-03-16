package com.example.ihave2.util;

import android.content.Context;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferences {
    private static android.content.SharedPreferences mSharedPreferences;



    public static android.content.SharedPreferences initiateSharedPreferences(){
        Context context;
        context= ContextSingleton.getContekst();
        try {
            return EncryptedSharedPreferences.create(
                    "secret_shared_prefs2",
                    getMasterKey(),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static String getMasterKey() {
        try {
            return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

