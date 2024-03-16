package com.example.ihave2.models.IntentExtra;

import android.os.Parcel;
import android.os.Parcelable;


public class PhotoActivityIntentExtra implements Parcelable {

//#nytfelt

    // unik id for photo på tværs af users
    private String photoName; //filnavn på foto på enheden
    private String plantTitle;
    private boolean editMode;


    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.photoName);
        dest.writeString(this.plantTitle);
        dest.writeByte(this.editMode ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.photoName = source.readString();
        this.plantTitle = source.readString();
        this.editMode = source.readByte() != 0;
    }

    public PhotoActivityIntentExtra() {
    }

    protected PhotoActivityIntentExtra(Parcel in) {
        this.photoName = in.readString();
        this.plantTitle = in.readString();
        this.editMode = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoActivityIntentExtra> CREATOR = new Parcelable.Creator<PhotoActivityIntentExtra>() {
        @Override
        public PhotoActivityIntentExtra createFromParcel(Parcel source) {
            return new PhotoActivityIntentExtra(source);
        }

        @Override
        public PhotoActivityIntentExtra[] newArray(int size) {
            return new PhotoActivityIntentExtra[size];
        }
    };

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPlantTitle() {
        return plantTitle;
    }

    public void setPlantTitle(String plantTitle) {
        this.plantTitle = plantTitle;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
