package com.example.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class PlantWithListsDto implements Parcelable{
    //#nytfelt
    private int plantId;
    private int userId;
    private String title;
    private String content;
    private String createdTime;
    private String mainPhotoName;
    private int category;

    private boolean deleted;

    @Relation(
            parentColumn = "plantId",
            entityColumn = "userId"
    )
    private List<PlantFlowerMonth> plantFlowerMonths;
    @Relation(
            parentColumn = "plantId",
            entityColumn = "userId"
    )
    private List<PlantPhoto> plantPhotos;

    protected PlantWithListsDto(Parcel in) {
        plantId = in.readInt();
        userId=in.readInt();
        title = in.readString();
        content = in.readString();
        createdTime = in.readString();
        mainPhotoName = in.readString();
        category = in.readInt();
        plantFlowerMonths = in.createTypedArrayList(PlantFlowerMonth.CREATOR);
        plantPhotos = in.createTypedArrayList(PlantPhoto.CREATOR);
    }
    public PlantWithListsDto(PlantWithListsDto plantWithLists){
        //this.plant=new Plant(plantWithLists.plant);
        this.plantFlowerMonths=new ArrayList<>(plantWithLists.plantFlowerMonths);
        this.plantPhotos=new ArrayList<>(plantWithLists.plantPhotos);
    }
    public PlantWithListsDto(){
    }


    public static final Creator<PlantWithListsDto> CREATOR = new Creator<PlantWithListsDto>() {
        @Override
        public PlantWithListsDto createFromParcel(Parcel in) {
            return new PlantWithListsDto(in);
        }

        @Override
        public PlantWithListsDto[] newArray(int size) {
            return new PlantWithListsDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeParcelable(plant, i);
        parcel.writeTypedList(plantFlowerMonths);
        parcel.writeTypedList(plantPhotos);
    }


    public int getPlantId() {return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getUserId() { return userId;}

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<PlantFlowerMonth> getPlantFlowerMonths() {
        return plantFlowerMonths;
    }

    public void setPlantFlowerMonths(List<PlantFlowerMonth> plantFlowerMonths) {
        this.plantFlowerMonths = plantFlowerMonths;
    }

    public List<PlantPhoto> getPlantPhotos() {
        return plantPhotos;
    }

    public void setPlantPhotos(List<PlantPhoto> plantPhotos) {
        this.plantPhotos = plantPhotos;
    }
}