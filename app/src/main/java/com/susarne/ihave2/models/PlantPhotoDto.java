package com.susarne.ihave2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;


public class PlantPhotoDto {

//#nytfelt

    private int photoId;
    private int plantId;
    private int userId;
    @ColumnInfo(name = "photoName")
    @NonNull
    private String photoName;
    private String uploadedPhotoReference;
    private boolean mainPhoto;
    private boolean deleted;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(@NonNull String photoName) {
        this.photoName = photoName;
    }

    public String getUploadedPhotoReference() {
        return uploadedPhotoReference;
    }

    public void setUploadedPhotoReference(String uploadedPhotoReference) {
        this.uploadedPhotoReference = uploadedPhotoReference;
    }

    public boolean isMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(boolean mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
