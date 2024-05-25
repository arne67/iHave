package com.susarne.ihave2.models;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "plantPhotos",
        foreignKeys = @ForeignKey(entity=Plant.class, parentColumns={"plantId"}, childColumns={"plantId"},onDelete = CASCADE)
)
public  class PlantPhoto implements Parcelable {

//#nytfelt

    @NonNull
    @PrimaryKey
    // unik id for photo på tværs af users
    private String photoId; //unik id for foto på tværs af users
    private String plantId; //unik id for plante på tværs af users
    private String createdBy; //unik id for users
    @ColumnInfo(name = "photoName")
    @NonNull
    private String photoName; //filnavn på foto på enheden
    private String uploadedPhotoReference; //Id på googles MediaItem
    private boolean photoUploaded; //er selve fotoet uploaded til google photo
    private boolean mainPhoto; //er dette det primære foto til plantne
    private boolean deleted; //er det slet-markeret
    private boolean createdInCloud; //er det oprettet i cloud-db'en
    private boolean syncedWithCloud; //er det i sync med cloud-db'en


    public PlantPhoto(String photoId, String plantId, String createdBy, @NonNull String photoName, String uploadedPhotoReference, String imageUrl, boolean photoUploaded, boolean mainPhoto, boolean deleted, boolean createdInCloud, boolean syncedWithCloud) {
        this.photoId = photoId;
        this.plantId = plantId;
        this.createdBy = createdBy;
        this.photoName = photoName;
        this.uploadedPhotoReference = uploadedPhotoReference;
        this.photoUploaded = photoUploaded;
        this.mainPhoto = mainPhoto;
        this.deleted = deleted;
        this.createdInCloud = createdInCloud;
        this.syncedWithCloud = syncedWithCloud;
    }


    public PlantPhoto() {
    }


    public PlantPhoto(PlantPhoto other) {
        this.photoId = other.photoId;
        this.plantId = other.plantId;
        this.createdBy = other.createdBy;
        this.photoName = other.photoName;
        this.uploadedPhotoReference = other.uploadedPhotoReference;
        this.photoUploaded = other.photoUploaded;
        this.mainPhoto = other.mainPhoto;
        this.deleted = other.deleted;
        this.createdInCloud = other.createdInCloud;
        this.syncedWithCloud = other.syncedWithCloud;
    }

    public PlantPhoto(PlantPhotoDto other) {
        //this.photoUploaded = other.photoUploaded;
        //this.createdInCloud = other.createdInCloud;
        //this.syncedWithCloud = other.syncedWithCloud;
        this.photoId = other.getPhotoId();
        this.plantId = other.getPlantId();
        this.createdBy = other.getCreatedBy();
        this.photoName = other.getPhotoName();
        this.uploadedPhotoReference = other.getUploadedPhotoReference();
        this.mainPhoto = other.isMainPhoto();
        this.deleted = other.isDeleted();
    }


    protected PlantPhoto(Parcel in) {
        photoId = in.readString();
        plantId = in.readString();
        createdBy = in.readString();
        photoName = in.readString();
        uploadedPhotoReference = in.readString();
        photoUploaded= in.readBoolean();
        mainPhoto= in.readBoolean();
        deleted= in.readBoolean();
        createdInCloud=in.readBoolean();
        syncedWithCloud = in.readBoolean();

    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(photoId);
        parcel.writeString(plantId);
        parcel.writeString(createdBy);
        parcel.writeString(photoName);
        parcel.writeString(uploadedPhotoReference);
        parcel.writeBoolean(photoUploaded);
        parcel.writeBoolean(mainPhoto);
        parcel.writeBoolean(deleted);
        parcel.writeBoolean(createdInCloud);
        parcel.writeBoolean(syncedWithCloud);
    }

    public static final Creator<PlantPhoto> CREATOR = new Creator<PlantPhoto>() {
        @Override
        public PlantPhoto createFromParcel(Parcel in) {
            return new PlantPhoto(in);
        }

        @Override
        public PlantPhoto[] newArray(int size) {
            return new PlantPhoto[size];
        }
    };

    public String getPlantId() {
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

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUploadedPhotoReference() {
        return uploadedPhotoReference;
    }

    public void setUploadedPhotoReference(String uploadedPhotoReference) {
        this.uploadedPhotoReference = uploadedPhotoReference;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSyncedWithCloud() {
        return syncedWithCloud;
    }

    public void setSyncedWithCloud(boolean syncedWithCloud) {
        this.syncedWithCloud = syncedWithCloud;
    }

    public boolean isPhotoUploaded() {
        return photoUploaded;
    }

    public void setPhotoUploaded(boolean photoUploaded) {
        this.photoUploaded = photoUploaded;
    }

    public boolean isMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(boolean mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public boolean isCreatedInCloud() {
        return createdInCloud;
    }

    public void setCreatedInCloud(boolean createdInCloud) {
        this.createdInCloud = createdInCloud;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString() {
        return "PlantPhoto{" +
                "photoId=" + photoId +
                ", plantId=" + plantId +
                ", userId=" + createdBy +
                ", photoName='" + photoName + '\'' +
                ", uploadedPhotoReference='" + uploadedPhotoReference + '\'' +
                ", photoUploaded=" + photoUploaded +
                ", mainPhoto=" + mainPhoto +
                ", deleted=" + deleted +
                ", createdInCloud=" + createdInCloud +
                ", syncedWithCloud=" + syncedWithCloud +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantPhoto that = (PlantPhoto) o;
        return photoId == that.photoId && plantId == that.plantId && createdBy == that.createdBy && photoUploaded == that.photoUploaded && mainPhoto == that.mainPhoto && deleted == that.deleted && createdInCloud == that.createdInCloud && syncedWithCloud == that.syncedWithCloud && photoName.equals(that.photoName) && Objects.equals(uploadedPhotoReference, that.uploadedPhotoReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoId, plantId, createdBy, photoName, uploadedPhotoReference, photoUploaded, mainPhoto, deleted, createdInCloud, syncedWithCloud);
    }
}
