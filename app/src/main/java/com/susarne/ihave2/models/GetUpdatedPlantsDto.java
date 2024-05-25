package com.susarne.ihave2.models;

import java.util.List;

public class GetUpdatedPlantsDto {
    //#nytfelt
    private String updatedUntil;

    private List<Plant> plants;
    private List<PlantPhoto> plantPhotos;




    public String getUpdatedUntil() {
        return updatedUntil;
    }

    public void setUpdatedUntil(String updatedUntil) {
        this.updatedUntil = updatedUntil;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<PlantPhoto> getPlantPhotos() {
        return plantPhotos;
    }

    public void setPlantPhotos(List<PlantPhoto> plantPhotos) {
        this.plantPhotos = plantPhotos;
    }
}