package com.susarne.ihave2.models;

public class PlantFlowerMonthDto {

//#nytfelt

    private int plantFlowerMonthId; //entydig ID på markeringen
    private int plantId; //
    private int userId;
    private int monthNo;
    private boolean deleted;

    public int getPlantFlowerMonthId() {
        return plantFlowerMonthId;
    }

    public void setPlantFlowerMonthId(int plantFlowerMonthId) {
        this.plantFlowerMonthId = plantFlowerMonthId;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMonthNo() {
        return monthNo;
    }

    public void setMonthNo(int monthNo) {
        this.monthNo = monthNo;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
