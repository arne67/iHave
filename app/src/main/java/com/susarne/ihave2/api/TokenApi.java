package com.susarne.ihave2.api;


import com.susarne.ihave2.models.TokenRefreshDto;
import com.susarne.ihave2.models.TokenRefreshRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TokenApi {
    //String BASE_URL = "https://susarne.dk/have/api/";
    //@GET("kategori")
    String BASE_URL = "https://oauth2.googleapis.com/";

    @POST("token")
    Call<TokenRefreshDto> refreshToken(@Body TokenRefreshRequestDto tokenRefreshRequestDto);

}
