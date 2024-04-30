package com.susarne.ihave2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GooglePhotoApiClient {

    private static GooglePhotoApiClient instance = null;
    private final GooglePhotoApi myApi;

    private GooglePhotoApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(interceptor);

        //httpClient.authenticator(new TokenAuthenticator());


        Retrofit retrofit = new Retrofit.Builder().baseUrl(GooglePhotoApi.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(GooglePhotoApi.class);
    }

    public static synchronized GooglePhotoApiClient getInstance() {
        if (instance == null) {
            instance = new GooglePhotoApiClient();
        }
        return instance;
    }

    public GooglePhotoApi getMyApi() {
        return myApi;
    }
}