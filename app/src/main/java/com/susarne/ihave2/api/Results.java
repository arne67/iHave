package com.susarne.ihave2.api;

import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("Title")
    private final String title;


    public Results(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}