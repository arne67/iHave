package com.susarne.ihave2.models;

import java.util.List;

public class EntirePlantDto {
    //#nytfelt

    private PlantDto plantDto;

    private List<PlantPhotoDto> plantPhotosDto;


    public EntirePlantDto(PlantWithLists other) {
        this.plantDto = new PlantDto(other.plant);
        for (PlantPhoto p: other.plantPhotos) {
            PlantPhotoDto plantPhotoDto = new PlantPhotoDto(p);
            plantPhotosDto.add(plantPhotoDto);
        }
    }

    public PlantDto getPlantDto() {
        return plantDto;
    }

    public void setPlantDto(PlantDto plantDto) {
        this.plantDto = plantDto;
    }

    public List<PlantPhotoDto> getPlantPhotosDto() {
        return plantPhotosDto;
    }

    public void setPlantPhotosDto(List<PlantPhotoDto> plantPhotosDto) {
        this.plantPhotosDto = plantPhotosDto;
    }
}