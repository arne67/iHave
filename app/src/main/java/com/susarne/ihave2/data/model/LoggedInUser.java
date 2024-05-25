package com.susarne.ihave2.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String name;
    private String accessToken;

    public LoggedInUser(String userId, String name, String accessToken) {
        this.userId = userId;
        this.name = name;
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }
}