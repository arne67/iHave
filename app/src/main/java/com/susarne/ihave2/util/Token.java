package com.susarne.ihave2.util;

import static com.susarne.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.susarne.ihave2.R;
import com.susarne.ihave2.api.TokenAuthState;
import com.susarne.ihave2.models.TokenStrings;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Token {
    private static final String TAG = "Token";
    private static SharedPreferences mSharedPreferences;
    private static String mMasterKeyAlias;


    //Hjælprutiner til Oauth 2.0 PKCE flow

    public static String getVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        String verifier = Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "hentAccessToken: verifier "+verifier);
        return verifier;
    }

    public static String getCodeChallenge(String verifier) {
        byte[] codeVerifierBytes = new byte[0];
        codeVerifierBytes = verifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(codeVerifierBytes);
        byte[] codeChallengeBytes = md.digest();
        String codeChallenge = Base64.encodeToString(codeChallengeBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "hentAccessToken: codeChallenge "+codeChallenge);
        return codeChallenge;
    }
    public static String getAccessToken(String name){
        SharedPreferences sharedPreferences;
        mSharedPreferences=initiateSharedPreferences();
        return mSharedPreferences.getString(name, null);
    }

    public static String getRefreshToken(String name){
        SharedPreferences sharedPreferences;
        mSharedPreferences=initiateSharedPreferences();
        return mSharedPreferences.getString(name, null);
    }

    public static void putAccessToken(String accessToken, String name){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(name, accessToken).apply();
    }

    public static void putRefreshToken(String refreshToken, String name){
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().putString(name, refreshToken).apply();
    }



    public static AuthorizationService startOAuthAuthentication(String scope, ActivityResultLauncher<Intent> oAuthActivityResultLauncher, Context context) {
        // Create the service coniguration
        // Note: The app does not have a token endpoint. An empty Uri value is used for the tokenEndpoint param
        Log.d(TAG, "startOAuthAuthentication: 1");
        AuthorizationServiceConfiguration mAuthServiceConfig;
        mAuthServiceConfig =
                new AuthorizationServiceConfiguration(
                        Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"),  // authorization_endpoint
                        Uri.parse("https://oauth2.googleapis.com/token"));                         // token_endpoint

        // Build the request


        String clientId=getClientId(context);
        Log.d(TAG, "startOAuthAuthentication: clientId"+clientId);
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                mAuthServiceConfig,
                clientId,
                ResponseTypeValues.CODE,
                Uri.parse("com.susarne.ihave2:/oauth2redirect"));

        Log.d(TAG, "startOAuthAuthentication: " + authRequestBuilder);


        String verifier = Token.getVerifier();
        String codeChallenge = Token.getCodeChallenge(verifier);
        AuthorizationRequest authRequest = authRequestBuilder
                .setScope(scope)
                .setCodeVerifier(verifier, codeChallenge, AuthorizationRequest.CODE_CHALLENGE_METHOD_S256)
                .setLoginHint(null)
                .build();
        Log.d(TAG, "startOAuthAuthentication: " + authRequest);

        // Start Authorization
        AuthorizationService mAuthorizationService = new AuthorizationService(context);
        Log.d(TAG, "startOAuthAuthentication: " + mAuthorizationService);
        Intent authIntent = mAuthorizationService.getAuthorizationRequestIntent(authRequest);
        Log.d(TAG, "startOAuthAuthentication: " + authIntent);

        //starter consent screen
        oAuthActivityResultLauncher.launch(authIntent);
        return mAuthorizationService;
    }
    public static TokenStrings receivedTokenResponse(TokenResponse tokenResponse,
                                                     AuthorizationException authException,
                                                     AuthState mAuthState) {
        Log.d(TAG, "Token request complete");
        if (tokenResponse != null) {
            mAuthState.update(tokenResponse, authException);
            // persist auth state to SharedPreferences
            if(mAuthState==null) Log.d(TAG, "receivedTokenResponse: mauthstate er null");
            TokenAuthState.putAuthState(mAuthState);
            //mSharedPreferences.edit().putString(Constants.AUTH_STATE, mAuthState.jsonSerializeString()).apply();
            TokenStrings tokenStrings = new TokenStrings();
            tokenStrings.setAccessTokenString(mAuthState.getAccessToken());
            tokenStrings.setRefreshTokenString(mAuthState.getRefreshToken());
            Long exp = mAuthState.getAccessTokenExpirationTime();
            return tokenStrings;
        } else {
            Log.d(TAG, " ", authException);
            return null;
        }
    }

    public static String getClientId(Context context){
        return context.getResources().getString(R.string.oauthClientId);
    }

}
