package com.susarne.ihave2.models;

import java.util.List;

public class EntirePlantsDto {
    //#nytfelt
    private List<EntirePlantDto> entirePlantDtos;

    public List<EntirePlantDto> getEntirePlantDtos() {
        return entirePlantDtos;
    }

    public void setEntirePlantDtos(List<EntirePlantDto> entirePlantDtos) {
        this.entirePlantDtos = entirePlantDtos;
    }
}