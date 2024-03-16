package com.example.ihave2.api;


import com.example.ihave2.models.GooglePhotos.MediaItem;
import com.example.ihave2.models.test.PostItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TestApi {
    String token="xx";
    //String BASE_URL = "https://susarne.dk/have/api/";
    //@GET("kategori")
    String BASE_URL = "https://jsonplaceholder.typicode.com/";

    @GET("posts/{postId}")
    Call<PostItem> getPost(@Path("postId") String postId);

    @GET("mediaItems/{mediaitemid}")
    Call<MediaItem> getMediaItem(@Header("Authorization") String token, @Path("mediaitemid") String mediaItemId);



    //Call<List<Results>> getsuperHeroes(@Header("Authorization") String token);
}

//public interface Api {
//
//    String BASE_URL = "https://simplifiedcoding.net/demos/";
//    @GET("marvel")
//    Call<List<Results>> getsuperHeroes();
//}