package com.susarne.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantWithLists implements Parcelable{
    @Embedded
    public Plant plant;
    @Relation(
            parentColumn = "plantId",
            entityColumn = "plantId"
    )
    public List<PlantFlowerMonth> plantFlowerMonths;
    @Relation(
            parentColumn = "plantId",
            entityColumn = "plantId"
    )
    public List<PlantPhoto> plantPhotos;

    protected PlantWithLists(Parcel in) {
        plant = in.readParcelable(Plant.class.getClassLoader());
        plantFlowerMonths = in.createTypedArrayList(PlantFlowerMonth.CREATOR);
        plantPhotos = in.createTypedArrayList(PlantPhoto.CREATOR);
    }
    public PlantWithLists(PlantWithLists plantWithLists){
        this.plant=new Plant(plantWithLists.plant);
        this.plantFlowerMonths=new ArrayList<PlantFlowerMonth>();

        for (PlantFlowerMonth p:plantWithLists.plantFlowerMonths) {
            PlantFlowerMonth plantFlowerMonth = new PlantFlowerMonth();
            plantFlowerMonth.setPlantFlowerMonthId(p.getPlantFlowerMonthId());
            plantFlowerMonth.setPlantId(p.getPlantId());
            plantFlowerMonth.setMonthNo(p.getMonthNo());
            plantFlowerMonth.setUserId(p.getUserId());
            plantFlowerMonth.setDeleted(p.isDeleted());
            plantFlowerMonth.setCreatedInCloud(p.isCreatedInCloud());
            plantFlowerMonth.setSyncedWithCloud(p.isSyncedWithCloud());
            plantFlowerMonth.setUploaded(p.isUploaded());
            this.plantFlowerMonths.add(plantFlowerMonth);
        }

        this.plantPhotos=new ArrayList<>(plantWithLists.plantPhotos);
    }
    public PlantWithLists(){
    }


    public static final Creator<PlantWithLists> CREATOR = new Creator<PlantWithLists>() {
        @Override
        public PlantWithLists createFromParcel(Parcel in) {
            return new PlantWithLists(in);
        }

        @Override
        public PlantWithLists[] newArray(int size) {
            return new PlantWithLists[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(plant, i);
        parcel.writeTypedList(plantFlowerMonths);
        parcel.writeTypedList(plantPhotos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantWithLists that = (PlantWithLists) o;
        return Objects.equals(plant, that.plant) && Objects.equals(plantFlowerMonths, that.plantFlowerMonths) && Objects.equals(plantPhotos, that.plantPhotos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plant, plantFlowerMonths, plantPhotos);
    }

    @Override
    public String toString() {
        return "PlantWithLists{" +
                "plant=" + plant +
                ", plantFlowerMonths=" + plantFlowerMonths +
                ", plantPhotos=" + plantPhotos +
                '}';
    }
}