package com.example.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenRefreshDto implements Parcelable{
    //#nytfelt
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;

    protected TokenRefreshDto(Parcel in) {
        access_token = in.readString();
        expires_in = in.readInt();
        scope = in.readString();
        token_type = in.readString();
    }

    public static final Creator<TokenRefreshDto> CREATOR = new Creator<TokenRefreshDto>() {
        @Override
        public TokenRefreshDto createFromParcel(Parcel in) {
            return new TokenRefreshDto(in);
        }

        @Override
        public TokenRefreshDto[] newArray(int size) {
            return new TokenRefreshDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(access_token);
        parcel.writeInt(expires_in);
        parcel.writeString(scope);
        parcel.writeString(token_type);
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}