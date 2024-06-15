package com.susarne.ihave2.models;

public class PlantDto {

//#nytfelt

    private String plantId;

    private String createdBy;


    private String title;

    private String content;

    private String createdTime;

    private String mainPhotoName;

    private int category;
    private boolean deleted;
    private boolean bloomsMonth1;
    private boolean bloomsMonth2;
    private boolean bloomsMonth3;
    private boolean bloomsMonth4;
    private boolean bloomsMonth5;
    private boolean bloomsMonth6;
    private boolean bloomsMonth7;
    private boolean bloomsMonth8;
    private boolean bloomsMonth9;
    private boolean bloomsMonth10;
    private boolean bloomsMonth11;
    private boolean bloomsMonth12;
    private String createdAt;

    public PlantDto() {
    }

    public PlantDto(Plant other) {
        this.plantId = other.getPlantId();
        this.createdBy = other.getCreatedBy();
        this.title = other.getTitle();
        this.content = other.getContent();
        this.createdTime = other.getCreatedTime();
        this.mainPhotoName = other.getMainPhotoName();
        this.category = other.getCategory();
        this.deleted = other.isDeleted();
        this.bloomsMonth1 = other.isBloomsMonth1();
        this.bloomsMonth2 = other.isBloomsMonth2();
        this.bloomsMonth3 = other.isBloomsMonth3();
        this.bloomsMonth4 = other.isBloomsMonth4();
        this.bloomsMonth5 = other.isBloomsMonth5();
        this.bloomsMonth6 = other.isBloomsMonth6();
        this.bloomsMonth7 = other.isBloomsMonth7();
        this.bloomsMonth8 = other.isBloomsMonth8();
        this.bloomsMonth9 = other.isBloomsMonth9();
        this.bloomsMonth10 = other.isBloomsMonth10();
        this.bloomsMonth11 = other.isBloomsMonth11();
        this.bloomsMonth12 = other.isBloomsMonth12();
        this.createdAt = other.getCreatedAt();
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getMainPhotoName() {
        return mainPhotoName;
    }

    public void setMainPhotoName(String mainPhotoName) {
        this.mainPhotoName = mainPhotoName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isBloomsMonth1() {
        return bloomsMonth1;
    }

    public void setBloomsMonth1(boolean bloomsMonth1) {
        this.bloomsMonth1 = bloomsMonth1;
    }

    public boolean isBloomsMonth2() {
        return bloomsMonth2;
    }

    public void setBloomsMonth2(boolean bloomsMonth2) {
        this.bloomsMonth2 = bloomsMonth2;
    }

    public boolean isBloomsMonth3() {
        return bloomsMonth3;
    }

    public void setBloomsMonth3(boolean bloomsMonth3) {
        this.bloomsMonth3 = bloomsMonth3;
    }

    public boolean isBloomsMonth4() {
        return bloomsMonth4;
    }

    public void setBloomsMonth4(boolean bloomsMonth4) {
        this.bloomsMonth4 = bloomsMonth4;
    }

    public boolean isBloomsMonth5() {
        return bloomsMonth5;
    }

    public void setBloomsMonth5(boolean bloomsMonth5) {
        this.bloomsMonth5 = bloomsMonth5;
    }

    public boolean isBloomsMonth6() {
        return bloomsMonth6;
    }

    public void setBloomsMonth6(boolean bloomsMonth6) {
        this.bloomsMonth6 = bloomsMonth6;
    }

    public boolean isBloomsMonth7() {
        return bloomsMonth7;
    }

    public void setBloomsMonth7(boolean bloomsMonth7) {
        this.bloomsMonth7 = bloomsMonth7;
    }

    public boolean isBloomsMonth8() {
        return bloomsMonth8;
    }

    public void setBloomsMonth8(boolean bloomsMonth8) {
        this.bloomsMonth8 = bloomsMonth8;
    }

    public boolean isBloomsMonth9() {
        return bloomsMonth9;
    }

    public void setBloomsMonth9(boolean bloomsMonth9) {
        this.bloomsMonth9 = bloomsMonth9;
    }

    public boolean isBloomsMonth10() {
        return bloomsMonth10;
    }

    public void setBloomsMonth10(boolean bloomsMonth10) {
        this.bloomsMonth10 = bloomsMonth10;
    }

    public boolean isBloomsMonth11() {
        return bloomsMonth11;
    }

    public void setBloomsMonth11(boolean bloomsMonth11) {
        this.bloomsMonth11 = bloomsMonth11;
    }

    public boolean isBloomsMonth12() {
        return bloomsMonth12;
    }

    public void setBloomsMonth12(boolean bloomsMonth12) {
        this.bloomsMonth12 = bloomsMonth12;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
