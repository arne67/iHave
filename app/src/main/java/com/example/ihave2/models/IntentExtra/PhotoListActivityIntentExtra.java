package com.example.ihave2.models.IntentExtra;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.ihave2.models.PlantWithLists;


public class PhotoListActivityIntentExtra implements Parcelable {

//#nytfelt

    // unik id for photo på tværs af users
    private PlantWithLists plantWithLists;
    private boolean editMode;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.plantWithLists, flags);
        dest.writeByte(this.editMode ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.plantWithLists = source.readParcelable(PlantWithLists.class.getClassLoader());
        this.editMode = source.readByte() != 0;
    }

    public PhotoListActivityIntentExtra() {
    }

    protected PhotoListActivityIntentExtra(Parcel in) {
        this.plantWithLists = in.readParcelable(PlantWithLists.class.getClassLoader());
        this.editMode = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoListActivityIntentExtra> CREATOR = new Parcelable.Creator<PhotoListActivityIntentExtra>() {
        @Override
        public PhotoListActivityIntentExtra createFromParcel(Parcel source) {
            return new PhotoListActivityIntentExtra(source);
        }

        @Override
        public PhotoListActivityIntentExtra[] newArray(int size) {
            return new PhotoListActivityIntentExtra[size];
        }
    };

    public PlantWithLists getPlantWithLists() {
        return plantWithLists;
    }

    public void setPlantWithLists(PlantWithLists plantWithLists) {
        this.plantWithLists = plantWithLists;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
