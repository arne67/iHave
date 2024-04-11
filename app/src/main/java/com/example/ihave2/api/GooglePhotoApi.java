package com.example.ihave2.api;


import com.example.ihave2.models.GooglePhotos.Album;
import com.example.ihave2.models.GooglePhotos.AlbumsGetRespondBody;
import com.example.ihave2.models.GooglePhotos.BatchCreateRequestBody;
import com.example.ihave2.models.GooglePhotos.BatchCreateRespondBody;
import com.example.ihave2.models.GooglePhotos.MediaItem;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GooglePhotoApi {
    String token="xx";
    //String BASE_URL = "https://susarne.dk/have/api/";
    //@GET("kategori")
    String BASE_URL = "https://photoslibrary.googleapis.com/v1/";

    @GET("albums")
    Call<AlbumsGetRespondBody> getAlbums(@Header("Authorization") String token);

    @GET("mediaItems/{mediaitemid}")
    Call<MediaItem> getMediaItem(@Header("Authorization") String token, @Path("mediaitemid") String mediaItemId);

    @POST("albums")
    Call<Album> createAlbum(@Header("Authorization") String token,
                            @Body RequestBody requestBody);

    @Headers({
//            "Content-type: application/octet-stream",
            "X-Goog-Upload-Content-Type: image/jpeg",
            "X-Goog-Upload-Protocol: raw"
    })

    @POST("uploads")
    Call<String> uploadMedia(@Header("Authorization") String token,
                             @Body RequestBody requestBody);


    @POST("./mediaItems:batchCreate")
    Call<BatchCreateRespondBody> addToAlbum(@Header("Authorization") String token,
                                            @Body BatchCreateRequestBody batchCreateRequestBody);


    //Call<List<Results>> getsuperHeroes(@Header("Authorization") String token);
}

//public interface Api {
//
//    String BASE_URL = "https://simplifiedcoding.net/demos/";
//    @GET("marvel")
//    Call<List<Results>> getsuperHeroes();
//}