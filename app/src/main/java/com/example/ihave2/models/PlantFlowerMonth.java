package com.example.ihave2.models;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "plantFlowerMonths",
        foreignKeys = @ForeignKey(entity=Plant.class, parentColumns={"plantId"}, childColumns={"plantId"},onDelete = CASCADE)
)
public class PlantFlowerMonth implements Parcelable, Cloneable {

//#nytfelt


    @PrimaryKey(autoGenerate = true)
    private int plantFlowerMonthId; //entydig ID på markeringen
    private int plantId; //
    private int userId;
    @ColumnInfo(name = "monthNo")
    private int monthNo;
    private boolean deleted;
    private boolean uploaded;
    private boolean createdInCloud; //er det oprettet i cloud-db'en
    private boolean syncedWithCloud; //er det i sync med cloud-db'en


    public PlantFlowerMonth(int plantFlowerMonthId, int plantId, int userId, int monthNo, boolean deleted, boolean uploaded, boolean createdInCloud, boolean syncedWithCloud) {
        this.plantFlowerMonthId = plantFlowerMonthId;
        this.plantId = plantId;
        this.userId = userId;
        this.monthNo = monthNo;
        this.deleted = deleted;
        this.uploaded = uploaded;
        this.createdInCloud = createdInCloud;
        this.syncedWithCloud = syncedWithCloud;
    }

    public PlantFlowerMonth(PlantFlowerMonth plantFlowerMonth) {
        this.plantFlowerMonthId = plantFlowerMonth.getPlantFlowerMonthId();
        this.plantId = plantFlowerMonth.getPlantId();
        this.userId = plantFlowerMonth.getUserId();
        this.monthNo = plantFlowerMonth.getMonthNo();
        this.deleted = plantFlowerMonth.isDeleted();
        this.uploaded = plantFlowerMonth.isUploaded();
        this.createdInCloud = plantFlowerMonth.isCreatedInCloud();
        this.syncedWithCloud = plantFlowerMonth.isSyncedWithCloud();
    }


    @Ignore
    public PlantFlowerMonth() {

    }

//    @NonNull
//    @Override
//    public PlantFlowerMonth clone() throws CloneNotSupportedException {
//        PlantFlowerMonth clonedPlantFlowerMonth = null;
//        try {
//            clonedPlantFlowerMonth = (PlantFlowerMonth) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return clonedPlantFlowerMonth;
//    }

    protected PlantFlowerMonth(Parcel in) {
        plantFlowerMonthId = in.readInt();
        plantId = in.readInt();
        userId = in.readInt();
        monthNo = in.readInt();
        deleted = in.readBoolean();
        uploaded = in.readBoolean();
        createdInCloud = in.readBoolean();
        syncedWithCloud = in.readBoolean();
    }


    public static final Creator<PlantFlowerMonth> CREATOR = new Creator<PlantFlowerMonth>() {
        @Override
        public PlantFlowerMonth createFromParcel(Parcel in) {
            return new PlantFlowerMonth(in);
        }

        @Override
        public PlantFlowerMonth[] newArray(int size) {
            return new PlantFlowerMonth[size];
        }
    };

    public int getPlantFlowerMonthId() {
        return plantFlowerMonthId;
    }

    public void setPlantFlowerMonthId(int plantFlowerMonthId) {
        this.plantFlowerMonthId = plantFlowerMonthId;
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

    public int getMonthNo() {
        return monthNo;
    }

    public void setMonthNo(int monthNo) {
        this.monthNo = monthNo;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isCreatedInCloud() {
        return createdInCloud;
    }

    public void setCreatedInCloud(boolean createdInCloud) {
        this.createdInCloud = createdInCloud;
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
        parcel.writeInt(plantFlowerMonthId);
        parcel.writeInt(plantId);
        parcel.writeInt(userId);
        parcel.writeInt(monthNo);
        parcel.writeBoolean(deleted);
        parcel.writeBoolean(uploaded);
        parcel.writeBoolean(createdInCloud);
        parcel.writeBoolean(syncedWithCloud);
    }

    @Override
    public String toString() {
        return "PlantFlowerMonth{" +
                "plantFlowerMonthId=" + plantFlowerMonthId +
                ", plantId=" + plantId +
                ", userId=" + userId +
                ", monthNo=" + monthNo +
                ", deleted=" + deleted +
                ", uploaded=" + uploaded +
                ", createdInCloud=" + createdInCloud +
                ", syncedWithCloud=" + syncedWithCloud +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantFlowerMonth that = (PlantFlowerMonth) o;
        return plantFlowerMonthId == that.plantFlowerMonthId && plantId == that.plantId && userId == that.userId && monthNo == that.monthNo && deleted == that.deleted && uploaded == that.uploaded && createdInCloud == that.createdInCloud && syncedWithCloud == that.syncedWithCloud;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantFlowerMonthId, plantId, userId, monthNo, deleted, uploaded, createdInCloud, syncedWithCloud);
    }
}
