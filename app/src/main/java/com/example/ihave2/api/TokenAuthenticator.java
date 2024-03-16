package com.example.ihave2.api;

import static com.example.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.example.ihave2.util.Constants.REFRESH_TOKEN_PHOTO;

import android.content.Context;
import android.util.Log;

import com.example.ihave2.util.Token;
import com.example.ihave2.util.ContextSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    String TAG = "TokenAuthenticator";


    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Log.d(TAG, "authenticate: response.code "+ response.code());
        Log.d(TAG, "authenticate: responseCount "+ responseCount(response));
        if (responseCount(response)>2){
            return null;
        } else {
            String refreshToken = Token.getRefreshToken(REFRESH_TOKEN_PHOTO);
            String accessToken = getNewAccessToken(refreshToken);
            return response.request().newBuilder()
                    .header("Authorization",  "Bearer " +accessToken)
                    .build();
        }
    }

    public String getNewAccessToken(String refreshToken) {
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "getNewAccessToken: refreshToken "+refreshToken);
        Context context=ContextSingleton.getContekst();
        String clientId = Token.getClientId(context);
        RequestBody requestBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();
        Request request = new Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .header("Content-type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        try {
            Response response =         client.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                Log.d(TAG, "getNewAccessToken: resp"+json);
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                //JsonObject root = (JsonObject) jsonObject.get("data");
                String newAccessToken = jsonObject.get("access_token").getAsString();
                Token.putAccessToken(newAccessToken,ACCESS_TOKEN_PHOTO);
                if (jsonObject.get("refresh_token")!=null){
                    String newRefreshToken = jsonObject.get("refresh_token").getAsString();
                    Token.putRefreshToken(newRefreshToken,REFRESH_TOKEN_PHOTO);
                }
                return newAccessToken;
            } else {
                Log.e(TAG, "fail: " + response.body().string());
                return null;
            }
            } catch (IOException e) {
            Log.d(TAG, "getNewAccessToken: getnewaccesstokenfejl"+ e);
            e.printStackTrace();
        }
        return null;

    }
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }


}

