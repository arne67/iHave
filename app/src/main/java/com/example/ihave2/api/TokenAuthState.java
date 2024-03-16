package com.example.ihave2.api;

import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.ihave2.util.Constants;

import net.openid.appauth.AuthState;

import org.json.JSONException;

public class TokenAuthState {
    private static SharedPreferences mSharedPreferences;
    private static AuthState mAuthState;
    private static String mMasterKeyAlias;

    private static final String TAG="TokenAuthState";

    public static AuthState getAuthState(){
        SharedPreferences sharedPreferences;
        AuthState authState;

        mSharedPreferences=initiateSharedPreferences();
        String jsonString = mSharedPreferences.getString(Constants.AUTH_STATE, null);

        if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
            try {
                mAuthState = AuthState.jsonDeserialize(jsonString);
                Log.d(TAG, "restoreState: access token " + mAuthState.getAccessToken());
                Log.d(TAG, "restoreState: refresh token " + mAuthState.getRefreshToken());
                Log.d(TAG, "restoreState: access token exp " + mAuthState.getAccessTokenExpirationTime());

                return mAuthState;

            } catch (JSONException e) {
            }
        }
        return null;

    }

    public static void putAuthState(AuthState authState){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(Constants.AUTH_STATE, authState.jsonSerializeString()).apply();
    }




}
