package com.example.ihave2.util;


import static com.example.ihave2.util.Constants.ACCESS_TOKEN_DRIVE;
import static com.example.ihave2.util.Constants.DRIVE_IHAVE_FOLDER_NAME;
import static com.example.ihave2.util.Constants.GOOGLE_DRIVE_FOLDERID;
import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;
import static com.example.ihave2.util.Token.getAccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate use of Drive's create folder API */
public class GoogleDrive {
    private static SharedPreferences mSharedPreferences;
    private static String TAG="GoogleDrive";


    /**
     * Create new folder.
     *
     * @return Inserted folder id if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static String createDriveFolder(String parentFolderId, String folderName, Context context) {
        String folderId;

        // Build a new authorized API client service.
        Drive service = buildApiClientService();

        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        if (parentFolderId == null) {
            folderMetadata.setParents(Collections.singletonList(getBaseFolderId()));
        } else {
            folderMetadata.setParents(Collections.singletonList(parentFolderId));
        }
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File destinationFolder = service.files().create(folderMetadata)
                    .setFields("id")
                    .execute();
            folderId = destinationFolder.getId();

            return folderId;
        } catch (IOException e) {
            Log.d("GoogleDrive", "createDriveFile: oprettelse af folder gik galt ");
            throw new RuntimeException(e);
        }


    }

    public static String createDriveBaseFolder() {
        String folderId;
        // Build a new authorized API client service.
        Drive service = buildApiClientService();

        File folderMetadata = new File();
        folderMetadata.setName(DRIVE_IHAVE_FOLDER_NAME);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File destinationFolder = service.files().create(folderMetadata)
                    .setFields("id")
                    .execute();
            folderId = destinationFolder.getId();
            putBaseFolderId(folderId);
            return folderId;
        } catch (IOException e) {
            Log.d("GoogleDrive", "createDriveFile: oprettelse af folder gik galt ");
            throw new RuntimeException(e);
        }


    }

    public static void createDriveFile(String parentFolderId, String fileName, String localUri, Context context) {
        String folderId;

        // Build a new authorized API client service.
        Drive service = buildApiClientService();

        try {
            // File's metadata.
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            folderId = getBaseFolderId();

            if (parentFolderId == null) {
                fileMetadata.setParents(Collections.singletonList(getBaseFolderId()));
            } else {
                fileMetadata.setParents(Collections.singletonList(parentFolderId));
            }

            java.io.File filePath = new java.io.File(context.getExternalFilesDir(localUri) + "/"+fileName);
            Log.d(TAG, "createDriveFile: localfile: "+filePath.toString());

            FileContent mediaContent = new FileContent("application/json", filePath);
            //FileContent mediaContent = new FileContent("application/json", inputFile);
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());


        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            Log.d("GoogleDrive", "createDriveFile: kopiering af fil gik galt GoogleJsonResponseException");
            System.err.println("Unable to create file: " + e.getDetails());
        } catch (IOException e) {
            Log.d("GoogleDrive", "createDriveFile: kopiering af fil gik galt IOException");

        }
    }
     public static String getFolderId(String folderName) {
        Drive service = buildApiClientService();
        String baseFolderId=getBaseFolderId();
        String folderId = null;
         List<File> files = null;
         try {
             files = service.files().list()
             .setQ("'" + baseFolderId + "' in parents and mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'")
             .setFields("files(id)")
             .execute()
             .getFiles();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         if (files != null && !files.isEmpty()) {
            folderId = files.get(0).getId();
        }
        return folderId;
    }


    public static String getFileId(String folderId, String fileName) {
        Drive service = buildApiClientService();
        String baseFolderId=getBaseFolderId();
        List<File> files = null;
        try {
            files = service.files().list()
                    .setQ("'" + folderId + "' in parents and name='" + fileName + "'")
                    .setFields("files(id)")
                    .execute()
                    .getFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (files != null && !files.isEmpty()) {
            folderId = files.get(0).getId();
        }
        return folderId;
    }

    public static OutputStream downloadFile(String fileId) {
        Drive service = buildApiClientService();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }


    public static String getBaseFolderId() {
        mSharedPreferences = initiateSharedPreferences();
        String baseFolderId;
        baseFolderId = mSharedPreferences.getString(GOOGLE_DRIVE_FOLDERID, null);
        if (baseFolderId == null) {
            baseFolderId = createDriveBaseFolder();
        }
        return baseFolderId;
    }

    public static void putBaseFolderId(String folderId) {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().putString(GOOGLE_DRIVE_FOLDERID, folderId).apply();
    }

    private static Drive buildApiClientService() {
        String mAccessTokenString;
        mAccessTokenString = getAccessToken(ACCESS_TOKEN_DRIVE);
        AccessToken accessToken = new AccessToken(mAccessTokenString, null);

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        return new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();
    }
}

