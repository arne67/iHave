package com.example.ihave2.models;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "plantPhotos",
        foreignKeys = @ForeignKey(entity=Plant.class, parentColumns={"plantId"}, childColumns={"plantId"},onDelete = CASCADE)
)
public class PlantPhoto implements Parcelable {

//#nytfelt

    @PrimaryKey(autoGenerate = true)
    // unik id for photo på tværs af users
    private int photoId; //unik id for foto på tværs af users
    private int plantId; //unik id for plante på tværs af users
    private int userId; //unik id for users
    @ColumnInfo(name = "photoName")
    @NonNull
    private String photoName; //filnavn på foto på enheden
    private String uploadedPhotoReference; //Id på googles MediaItem
    private String imageUrl; //tænkt til  baseUrl fra googles MediaItem, men feltet er droppet da baseUrl expires
    private boolean photoUploaded; //er selve fotoet uploaded til google photo
    private boolean mainPhoto; //er dette det primære foto til plantne
    private boolean deleted; //er det slet-markeret
    private boolean createdInCloud; //er det oprettet i cloud-db'en
    private boolean syncedWithCloud; //er det i sync med cloud-db'en


    public PlantPhoto(int photoId, int plantId, int userId, @NonNull String photoName, String uploadedPhotoReference, String imageUrl, boolean photoUploaded, boolean mainPhoto, boolean deleted, boolean createdInCloud, boolean syncedWithCloud) {
        this.photoId = photoId;
        this.plantId = plantId;
        this.userId = userId;
        this.photoName = photoName;
        this.uploadedPhotoReference = uploadedPhotoReference;
        this.imageUrl = imageUrl;
        this.photoUploaded = photoUploaded;
        this.mainPhoto = mainPhoto;
        this.deleted = deleted;
        this.createdInCloud = createdInCloud;
        this.syncedWithCloud = syncedWithCloud;
    }

    @Ignore
    public PlantPhoto() {
    }


    protected PlantPhoto(Parcel in) {
        photoId = in.readInt();
        plantId = in.readInt();
        userId = in.readInt();
        photoName = in.readString();
        uploadedPhotoReference = in.readString();
        imageUrl=in.readString();
        photoUploaded= in.readBoolean();
        mainPhoto= in.readBoolean();
        deleted= in.readBoolean();
        createdInCloud=in.readBoolean();
        syncedWithCloud = in.readBoolean();

    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(photoId);
        parcel.writeInt(plantId);
        parcel.writeInt(userId);
        parcel.writeString(photoName);
        parcel.writeString(uploadedPhotoReference);
        parcel.writeString(imageUrl);
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

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
                ", userId=" + userId +
                ", photoName='" + photoName + '\'' +
                ", uploadedPhotoReference='" + uploadedPhotoReference + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
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
        return photoId == that.photoId && plantId == that.plantId && userId == that.userId && photoUploaded == that.photoUploaded && mainPhoto == that.mainPhoto && deleted == that.deleted && createdInCloud == that.createdInCloud && syncedWithCloud == that.syncedWithCloud && photoName.equals(that.photoName) && Objects.equals(uploadedPhotoReference, that.uploadedPhotoReference) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoId, plantId, userId, photoName, uploadedPhotoReference, imageUrl, photoUploaded, mainPhoto, deleted, createdInCloud, syncedWithCloud);
    }
}
