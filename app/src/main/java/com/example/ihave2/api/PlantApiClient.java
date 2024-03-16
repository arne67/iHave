package com.example.ihave2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PlantApiClient {

    private static PlantApiClient instance = null;
    private final PlantApi myApi;

    private PlantApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(interceptor);

        //httpClient.authenticator(new TokenAuthenticator());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(PlantApi.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(PlantApi.class);
    }

    public static synchronized PlantApiClient getInstance() {
        if (instance == null) {
            instance = new PlantApiClient();
        }
        return instance;
    }

    public PlantApi getMyApi() {
        return myApi;
    }
}