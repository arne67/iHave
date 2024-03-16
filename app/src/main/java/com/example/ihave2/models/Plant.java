package com.example.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Objects;

@Entity(tableName = "plants",primaryKeys = {"plantId"})
public class Plant implements Parcelable {

//#nytfelt

    private int plantId;

    private int userId;

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "timestamp")
    private String createdTime;
    @ColumnInfo(name = "mainPhotoName")
    private String mainPhotoName;
    @ColumnInfo(name = "category")
    private int category;
    private boolean createdInCloud;
    //deleted ændres aldrig tilbage til false når den først er sat true
    private boolean deleted;
    //uplaoded sættes til false ved enhver ændring på entiteten og sættes til true når upload går godt
    private boolean syncedWithCloud;



    public Plant(String title, String content, String createdTime, String mainPhotoName, int category, boolean createdInCloud, boolean deleted, boolean syncedWithCloud) {
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.mainPhotoName = mainPhotoName;
        this.category = category;
        this.createdInCloud = createdInCloud;
        this.deleted = deleted;
        this.syncedWithCloud = syncedWithCloud;
    }

    public Plant(Plant plant) {
        this.plantId =plant.getPlantId();
        this.userId =plant.getUserId();
        this.title = plant.getTitle();
        this.content = plant.getContent();
        this.createdTime = plant.getCreatedTime();
        this.mainPhotoName = plant.getMainPhotoName();
        this.category = plant.getCategory();
        this.createdInCloud = plant.isCreatedInCloud();
        this.deleted = plant.isDeleted();
        this.syncedWithCloud = plant.isSyncedWithCloud();
    }


    @Ignore
    public Plant() {
    }


    protected Plant(Parcel in) {
        plantId = in.readInt();
        userId = in.readInt();
        title = in.readString();
        content = in.readString();
        createdTime = in.readString();
        mainPhotoName = in.readString();
        category = in.readInt();
        createdInCloud = in.readBoolean();
        deleted = in.readBoolean();
        syncedWithCloud = in.readBoolean();
    }

    public static final Creator<Plant> CREATOR = new Creator<Plant>() {
        @Override
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        @Override
        public Plant[] newArray(int size) {
            return new Plant[size];
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
    public String getMainPhotoName() {
        return mainPhotoName;
    }

    public void setMainPhotoName(String mainPhotoName) {
        this.mainPhotoName = mainPhotoName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isCreatedInCloud() {
        return createdInCloud;
    }

    public void setCreatedInCloud(boolean createdInCloud) {
        this.createdInCloud = createdInCloud;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeInt(plantId);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(createdTime);
        parcel.writeString(mainPhotoName);
        parcel.writeInt(category);
        parcel.writeBoolean(createdInCloud);
        parcel.writeBoolean(deleted);
        parcel.writeBoolean(syncedWithCloud);
    }

    @Override
    public String toString() {
        return "Plant{" +
                ",plantId=" + plantId +
                ",userId=" + userId +
                ",title=" + title +
                ",content=" + content +
                ",createdTime=" + createdTime +
                ",mainPhotoName=" + mainPhotoName +
                ",category=" + category +
                ",createdInCloud=" + createdInCloud +
                ",deleted=" + deleted +
                ",syncedWithCloud=" + syncedWithCloud +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return plantId == plant.plantId && userId == plant.userId && category == plant.category && createdInCloud == plant.createdInCloud && deleted == plant.deleted && syncedWithCloud == plant.syncedWithCloud && Objects.equals(title, plant.title) && Objects.equals(content, plant.content) && Objects.equals(createdTime, plant.createdTime) && Objects.equals(mainPhotoName, plant.mainPhotoName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantId, userId, title, content, createdTime, mainPhotoName, category, createdInCloud, deleted, syncedWithCloud);
    }
}
