package com.susarne.ihave2.models;

import java.util.List;

public class PlantsWithListsDto{
    //#nytfelt
    private String updatedUntil;
    public List<PlantWithListsDto> plants;

    public String getUpdatedUntil() {
        return updatedUntil;
    }

    public void setUpdatedUntil(String updatedUntil) {
        this.updatedUntil = updatedUntil;
    }
}