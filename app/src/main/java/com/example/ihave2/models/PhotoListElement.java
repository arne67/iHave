package com.example.ihave2.models;


public class PhotoListElement {

//#nytfelt

    // unik id for photo på tværs af users
    private int idx;
    private PlantPhoto plantPhoto;


    public PhotoListElement(int idx, PlantPhoto plantPhoto) {
        this.idx = idx;
        this.plantPhoto = plantPhoto;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public PlantPhoto getPlantPhoto() {
        return plantPhoto;
    }

    public void setPlantPhoto(PlantPhoto plantPhoto) {
        this.plantPhoto = plantPhoto;
    }
}
