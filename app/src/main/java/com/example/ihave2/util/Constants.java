package com.example.ihave2.util;

public class Constants {
    public static final String SHARED_PREFERENCES_NAME = "AUTH_STATE_PREFERENCE";
    public static final String AUTH_STATE = "AUTH_STATE";
    public static final String ACCESS_TOKEN_PHOTO = "ACCESS_TOKEN_PHOTO";
    public static final String REFRESH_TOKEN_PHOTO = "REFRESH_TOKEN_PHOTO";
    public static final String ACCESS_TOKEN_DRIVE = "ACCESS_TOKEN_DRIVE";
    public static final String REFRESH_TOKEN_DRIVE = "REFRESH_TOKEN_DRIVE";
    public static final String IHAVE_USER_NAME = "IHAVE_USER_NAME";
    public static final String IHAVE_PASSWORD = "IHAVE_PASSWORD";
    public static final String GOOGLE_PHOTO_ALBUMID = "GOOGLE_PHOTO_ALBUMID";
    public static final String GOOGLE_DRIVE_FOLDERID = "GOOGLE_DRIVE_FOLDERID";


    public static final String SCOPE_PROFILE = "profile";
    public static final String SCOPE_EMAIL = "email";
    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_DRIVE = "https://www.googleapis.com/auth/drive";
    public static final String DATA_PICTURE = "picture";
    public static final String DATA_FIRST_NAME = "given_name";
    public static final String DATA_LAST_NAME = "family_name";
    public static final String DATA_EMAIL = "email";
    public static final String CODE_VERIFIER_CHALLENGE_METHOD = "S256";
    public static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    public static final String URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String URL_TOKEN_EXCHANGE = "https://www.googleapis.com/oauth2/v4/token";
    public static final String URL_AUTH_REDIRECT = "com.example.ihave2/oauth2redirect";
    public static final String URL_API_CALL = "https://www.googleapis.com/drive/v2/files";
    public static final String URL_LOGOUT = "https://accounts.google.com/o/oauth2/revoke?token=";
    private static final String URL_LOGOUT_REDIRECT = "com.example.ihave2:/logout";
    public static final String SAVE_PLANT_WORK_NAME = "save_plant_work";
    public static final String API_PHOTO="API_PHOTO";
    public static final String API_DRIVE="API_DRIVE";
    public static final String DRIVE_IHAVE_FOLDER_NAME="Haven3";


    public static final String WORKER_PLANT_ID ="worker_plant_id";
    public static long DELAY_TIME_MILLIS=1000;
    public static final String ACTION_NONE="0";
    public static final String ACTION_DELETE_PHOTO="1";
    public static final String ACTION_SELECT_MAIN_PHOTO="2";
}
