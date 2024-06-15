package com.susarne.ihave2.models.IntentExtra;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;

import java.util.List;


public class PhotoListActivityIntentExtra implements Parcelable {

//#nytfelt

    // unik id for photo på tværs af users
    private List<PlantPhoto> plantPhotos;
    private boolean editMode;
    String title;
    String mainPhotoName;


    public PhotoListActivityIntentExtra(Parcel in) {
        plantPhotos = in.createTypedArrayList(PlantPhoto.CREATOR);
        editMode = in.readByte() != 0;
        mainPhotoName = in.readString();
        title = in.readString();
    }

    public PhotoListActivityIntentExtra() {
    }

    public static final Creator<PhotoListActivityIntentExtra> CREATOR = new Creator<PhotoListActivityIntentExtra>() {
        @Override
        public PhotoListActivityIntentExtra createFromParcel(Parcel in) {
            return new PhotoListActivityIntentExtra(in);
        }

        @Override
        public PhotoListActivityIntentExtra[] newArray(int size) {
            return new PhotoListActivityIntentExtra[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(plantPhotos);
        parcel.writeByte((byte) (editMode ? 1 : 0));
        parcel.writeString(mainPhotoName);
        parcel.writeString(title);
    }

    @Override
    public String toString() {
        return "PhotoListActivityIntentExtra{" +
                "plantPhotos=" + plantPhotos +
                ", editMode=" + editMode +
                ", title='" + title + '\'' +
                ", mainPhotoName='" + mainPhotoName + '\'' +
                '}';
    }

    public List<PlantPhoto> getPlantPhotos() {
        return plantPhotos;
    }

    public void setPlantPhotos(List<PlantPhoto> plantPhotos) {
        this.plantPhotos = plantPhotos;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainPhotoName() {
        return mainPhotoName;
    }

    public void setMainPhotoName(String mainPhotoName) {
        this.mainPhotoName = mainPhotoName;
    }
}
