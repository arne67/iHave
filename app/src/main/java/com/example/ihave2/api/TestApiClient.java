package com.example.ihave2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TestApiClient {

    private static TestApiClient instance = null;
    private final TestApi myApi;

    private TestApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(interceptor);

        //httpClient.authenticator(new TokenAuthenticator());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(TestApi.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(TestApi.class);
    }

    public static synchronized TestApiClient getInstance() {
        if (instance == null) {
            instance = new TestApiClient();
        }
        return instance;
    }

    public TestApi getMyApi() {
        return myApi;
    }
}