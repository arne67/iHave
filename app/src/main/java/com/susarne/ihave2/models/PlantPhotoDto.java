package com.susarne.ihave2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;


public class PlantPhotoDto {

//#nytfelt

    private String photoId;
    private String plantId;
    private String createdBy;
    @ColumnInfo(name = "photoName")
    @NonNull
    private String photoName;
    private String uploadedPhotoReference;
    private boolean mainPhoto;
    private boolean deleted;

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String  getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public PlantPhotoDto() {
    }

    public PlantPhotoDto(PlantPhoto other) {
        this.photoId = other.getPhotoId();
        this.plantId = other.getPlantId();
        this.createdBy = other.getCreatedBy();
        this.photoName = other.getPhotoName();
        this.uploadedPhotoReference = other.getUploadedPhotoReference();
        this.mainPhoto = other.isMainPhoto();
        this.deleted = other.isDeleted();
    }
}
