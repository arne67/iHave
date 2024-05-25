package com.susarne.ihave2.api;


import androidx.annotation.Keep;

import com.susarne.ihave2.data.model.LoggedInUser;
import com.susarne.ihave2.models.EntirePlantsDto;
import com.susarne.ihave2.models.GetUpdatedPlantsDto;
import com.susarne.ihave2.models.GetUserRespondDto;
import com.susarne.ihave2.models.LoginRequestDto;
import com.susarne.ihave2.models.PhotoAlbumIdDto;
import com.susarne.ihave2.models.PlantDto;
import com.susarne.ihave2.models.PlantFlowerMonthDto;
import com.susarne.ihave2.models.PlantPhotoDto;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.models.PlantsWithListsDto;
import com.susarne.ihave2.models.RegisterRequestDto;
import com.susarne.ihave2.util.Utility;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

@Keep
public interface PlantApi {
    String token = "xx";
    //String BASE_URL = "https://susarne.dk/have/api/";
    //@GET("kategori")
    String BASE_URL = Utility.getIhaveCloudBaseUrl();

    @GET("plants/{userId}/{plantId}")
    Call<PlantWithListsDto> getPlant(@Path("userId") String userId, @Path("plantId") String plantId);

    @GET("plants/user/{userId}")
    Call<PlantsWithListsDto> getPlantsForUserId(@Path("userId") int userId);

    @GET("plants/community/user/{userId}")
    Call<PlantsWithListsDto> getPlantsInUsersCommunities(@Path("userId") String userId);

    @GET("plants/updates/{userId}/{lastGetUpdatedPlantsUntil}")
    Call<GetUpdatedPlantsDto> getUpdatedPlants(@Path("userId") int userId,
                                               @Path("lastGetUpdatedPlantsUntil") String lastGetUpdatedPlantsUntil);


    @POST("plants/")
    Call<PlantWithListsDto> createPlant(@Header("Authorization") String token,
                                        @Body PlantWithListsDto plantWithListsDto);

    @PUT("plants/{userId}/{plantId}")
    Call<PlantDto> updatePlant(@Header("Authorization") String token,
                               @Body PlantDto plantDto,
                               @Path("userId") String userId,
                               @Path("plantId") String plantId);

    @POST("plantflowermonths/")
    Call<PlantFlowerMonthDto> createPlantFlowerMonth(@Header("Authorization") String token,
                                                     @Body PlantFlowerMonthDto plantFlowerMonthDto);

    @PUT("plantflowermonths/{userId}/{plantFlowerMonthId}")
    Call<PlantFlowerMonthDto> updatePlantFlowerMonth(@Header("Authorization") String token,
                                                     @Body PlantFlowerMonthDto plantFlowerMonthDto,
                                                     @Path("userId") int userId, @Path("plantFlowerMonthId") int plantFlowerMonthId);

    @POST("plantphotos/")
    Call<PlantPhotoDto> createPlantPhoto(@Header("Authorization") String token,
                                         @Body PlantPhotoDto plantPhotoDto);

    @PUT("plantphotos/{userId}/{photoId}")
    Call<PlantPhotoDto> updatePlantPhoto(@Header("Authorization") String token,
                                         @Body PlantPhotoDto plantPhotoDto,
                                         @Path("userId") String userId, @Path("photoId") String photoId);


    @POST("users/login")
    Call<LoggedInUser> login(@Header("Authorization") String token,
                             @Body LoginRequestDto loginRequestDto);

    @POST("users/")
    Call<LoggedInUser> register(@Header("Authorization") String token,
                                @Body RegisterRequestDto registerRequestDto);

    @PATCH("users/{userId}/photo-album-id")
    Call<Void> updatePhotoAlbumId(@Header("Authorization") String token,
                                  @Path("userId") String userId,
                                  @Body PhotoAlbumIdDto photoAlbumIdDto);


    @GET("users/{userId}")
    Call<GetUserRespondDto> getUser(@Path("userId") String userId);


    //Call<List<Results>> getsuperHeroes(@Header("Authorization") String token);
}

//public interface Api {
//
//    String BASE_URL = "https://simplifiedcoding.net/demos/";
//    @GET("marvel")
//    Call<List<Results>> getsuperHeroes();
//}