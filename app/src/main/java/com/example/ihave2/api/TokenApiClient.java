package com.example.ihave2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TokenApiClient {

    private static TokenApiClient instance = null;
    private final TokenApi myApi;

    private TokenApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(interceptor);

        //httpClient.authenticator(new TokenAuthenticator());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(TokenApi.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(TokenApi.class);
    }

    public static synchronized TokenApiClient getInstance() {
        if (instance == null) {
            instance = new TokenApiClient();
        }
        return instance;
    }

    public TokenApi getMyApi() {
        return myApi;
    }
}