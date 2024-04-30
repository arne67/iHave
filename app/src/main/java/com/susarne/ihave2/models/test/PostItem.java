package com.susarne.ihave2.models.test;


import com.google.gson.annotations.SerializedName;

public class PostItem {

    @SerializedName("userId")
    public int userId;
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("body")
    public String body;


}
