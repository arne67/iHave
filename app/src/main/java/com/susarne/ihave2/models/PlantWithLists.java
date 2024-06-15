package com.susarne.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantWithLists {
    @Embedded
    public Plant plant;

    @Relation(
            parentColumn = "plantId",
            entityColumn = "plantId"
    )
    public List<PlantPhoto> plantPhotos;

    protected PlantWithLists(Parcel in) {
        plant = in.readParcelable(Plant.class.getClassLoader());
        plantPhotos = in.createTypedArrayList(PlantPhoto.CREATOR);
    }
    public PlantWithLists(PlantWithLists plantWithLists){
        this.plant=new Plant(plantWithLists.plant);
        this.plantPhotos=new ArrayList<>(plantWithLists.plantPhotos);
    }
    public PlantWithLists(){
    }

    public PlantWithLists(EntirePlantDto other){
        this.plant = new Plant(other.getPlantDto());
        for (PlantPhotoDto p: other.getPlantPhotosDto()) {
            PlantPhoto plantPhoto = new PlantPhoto(p);
            this.plantPhotos.add(plantPhoto);
        }
    }



//    public static final Creator<PlantWithLists> CREATOR = new Creator<PlantWithLists>() {
//        @Override
//        public PlantWithLists createFromParcel(Parcel in) {
//            return new PlantWithLists(in);
//        }
//
//        @Override
//        public PlantWithLists[] newArray(int size) {
//            return new PlantWithLists[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeParcelable(plant, i);
//        parcel.writeTypedList(plantPhotos);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantWithLists that = (PlantWithLists) o;
        return Objects.equals(plant, that.plant) && Objects.equals(plantPhotos, that.plantPhotos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plant, plantPhotos);
    }

    @Override
    public String toString() {
        return "PlantWithLists{" +
                "plant=" + plant +
                ", plantPhotos=" + plantPhotos +
                '}';
    }
}