package com.example.ihave2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class GooglePhotoApiScalarsClient {

    private static GooglePhotoApiScalarsClient instance = null;
    private final Api myApi;

    private GooglePhotoApiScalarsClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(interceptor);

        //httpClient.authenticator(new TokenAuthenticator());


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
                .client(httpClient.build())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized GooglePhotoApiScalarsClient getInstance() {
        if (instance == null) {
            instance = new GooglePhotoApiScalarsClient();
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }
}