package com.susarne.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenRefreshRequestDto implements Parcelable{
    //#nytfelt
    private String client_id;
    private String grant_type;
    private String refresh_token;

    public TokenRefreshRequestDto() {
    }

    protected TokenRefreshRequestDto(Parcel in) {
        client_id = in.readString();
        grant_type = in.readString();
        refresh_token = in.readString();
    }

    public static final Creator<TokenRefreshRequestDto> CREATOR = new Creator<TokenRefreshRequestDto>() {
        @Override
        public TokenRefreshRequestDto createFromParcel(Parcel in) {
            return new TokenRefreshRequestDto(in);
        }

        @Override
        public TokenRefreshRequestDto[] newArray(int size) {
            return new TokenRefreshRequestDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(client_id);
        parcel.writeString(grant_type);
        parcel.writeString(refresh_token);
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}