package com.susarne.ihave2.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String userId;
    private String accessToken;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String userId, String accessToken) {

        this.displayName = displayName;
        this.userId = userId;
        this.accessToken=accessToken;
    }

    String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}