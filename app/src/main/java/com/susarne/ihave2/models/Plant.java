package com.susarne.ihave2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Objects;

import javax.annotation.Nullable;

@Entity(tableName = "plants")
public class Plant {
//    public class Plant implements Parcelable {

//#nytfelt

    @NonNull
    @PrimaryKey
    private String plantId;

    private String createdBy;

    @ColumnInfo(name = "title")
    private String title;

    private String family;
    private String taxonId;
    private int heightFrom;
    private int heightTo;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "timestamp")
    private String createdTime;
    @ColumnInfo(name = "mainPhotoName")
    private String mainPhotoName;
    @ColumnInfo(name = "category")
    private int category;
    private boolean createdInCloud;
    //deleted ændres aldrig tilbage til false når den først er sat true
    private boolean deleted;
    //uplaoded sættes til false ved enhver ændring på entiteten og sættes til true når upload går godt
    private boolean syncedWithCloud;

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



    public Plant(Plant other) {
        this.plantId = other.plantId;
        this.createdBy = other.createdBy;
        this.title = other.title;
        this.family=other.family;
        this.taxonId=other.taxonId;
        this.heightFrom=other.heightFrom;
        this.heightTo=other.heightTo;
        this.content = other.content;
        this.createdTime = other.createdTime;
        this.mainPhotoName = other.mainPhotoName;
        this.category = other.category;
        this.createdInCloud = other.createdInCloud;
        this.deleted = other.deleted;
        this.syncedWithCloud = other.syncedWithCloud;
        this.bloomsMonth1 = other.bloomsMonth1;
        this.bloomsMonth2 = other.bloomsMonth2;
        this.bloomsMonth3 = other.bloomsMonth3;
        this.bloomsMonth4 = other.bloomsMonth4;
        this.bloomsMonth5 = other.bloomsMonth5;
        this.bloomsMonth6 = other.bloomsMonth6;
        this.bloomsMonth7 = other.bloomsMonth7;
        this.bloomsMonth8 = other.bloomsMonth8;
        this.bloomsMonth9 = other.bloomsMonth9;
        this.bloomsMonth10 = other.bloomsMonth10;
        this.bloomsMonth11 = other.bloomsMonth11;
        this.bloomsMonth12 = other.bloomsMonth12;
        this.createdAt = other.createdAt;
    }

    public Plant() {
    }

    public Plant(PlantDto other){
        //this.createdInCloud = other.createdInCloud;
        //this.syncedWithCloud = other.syncedWithCloud;
        this.plantId = other.getPlantId();
        this.createdBy = other.getCreatedBy();
        this.title = other.getTitle();
        this.family=other.getFamily();
        this.taxonId=other.getTaxonId();
        this.heightFrom=other.getHeightFrom();
        this.heightTo=other.getHeightTo();
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

    public Plant(PlantWithListsDto other) {
        this.plantId = other.getPlantId();
        this.createdBy = other.getCreatedBy();
        this.title = other.getTitle();
        this.family=other.getFamily();
        this.taxonId=other.getTaxonId();
        this.heightFrom=other.getHeightFrom();
        this.heightTo=other.getHeightTo();
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

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(String taxonId) {
        this.taxonId = taxonId;
    }

    public int getHeightFrom() {
        return heightFrom;
    }

    public void setHeightFrom(int heightFrom) {
        this.heightFrom = heightFrom;
    }

    public int getHeightTo() {
        return heightTo;
    }

    public void setHeightTo(int heightTo) {
        this.heightTo = heightTo;
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

    public boolean isCreatedInCloud() {
        return createdInCloud;
    }

    public void setCreatedInCloud(boolean createdInCloud) {
        this.createdInCloud = createdInCloud;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSyncedWithCloud() {
        return syncedWithCloud;
    }

    public void setSyncedWithCloud(boolean syncedWithCloud) {
        this.syncedWithCloud = syncedWithCloud;
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

    public boolean bloomsInCurrentMonth() {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Kalender måned er 0-baseret
        switch (currentMonth) {
            case 1: return bloomsMonth1;
            case 2: return bloomsMonth2;
            case 3: return bloomsMonth3;
            case 4: return bloomsMonth4;
            case 5: return bloomsMonth5;
            case 6: return bloomsMonth6;
            case 7: return bloomsMonth7;
            case 8: return bloomsMonth8;
            case 9: return bloomsMonth9;
            case 10: return bloomsMonth10;
            case 11: return bloomsMonth11;
            case 12: return bloomsMonth12;
            default: return false;
        }
    }

    @Override
    public String toString() {
        return "Plant{" +
                "plantId='" + plantId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", title='" + title + '\'' +
                ", family='" + family + '\'' +
                ", taxonId='" + taxonId + '\'' +
                ", heightFrom=" + heightFrom +
                ", heightTo=" + heightTo +
                ", content='" + content + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", mainPhotoName='" + mainPhotoName + '\'' +
                ", category=" + category +
                ", createdInCloud=" + createdInCloud +
                ", deleted=" + deleted +
                ", syncedWithCloud=" + syncedWithCloud +
                ", bloomsMonth1=" + bloomsMonth1 +
                ", bloomsMonth2=" + bloomsMonth2 +
                ", bloomsMonth3=" + bloomsMonth3 +
                ", bloomsMonth4=" + bloomsMonth4 +
                ", bloomsMonth5=" + bloomsMonth5 +
                ", bloomsMonth6=" + bloomsMonth6 +
                ", bloomsMonth7=" + bloomsMonth7 +
                ", bloomsMonth8=" + bloomsMonth8 +
                ", bloomsMonth9=" + bloomsMonth9 +
                ", bloomsMonth10=" + bloomsMonth10 +
                ", bloomsMonth11=" + bloomsMonth11 +
                ", bloomsMonth12=" + bloomsMonth12 +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return heightFrom == plant.heightFrom && heightTo == plant.heightTo && category == plant.category && createdInCloud == plant.createdInCloud && deleted == plant.deleted && syncedWithCloud == plant.syncedWithCloud && bloomsMonth1 == plant.bloomsMonth1 && bloomsMonth2 == plant.bloomsMonth2 && bloomsMonth3 == plant.bloomsMonth3 && bloomsMonth4 == plant.bloomsMonth4 && bloomsMonth5 == plant.bloomsMonth5 && bloomsMonth6 == plant.bloomsMonth6 && bloomsMonth7 == plant.bloomsMonth7 && bloomsMonth8 == plant.bloomsMonth8 && bloomsMonth9 == plant.bloomsMonth9 && bloomsMonth10 == plant.bloomsMonth10 && bloomsMonth11 == plant.bloomsMonth11 && bloomsMonth12 == plant.bloomsMonth12 && Objects.equals(plantId, plant.plantId) && Objects.equals(createdBy, plant.createdBy) && Objects.equals(title, plant.title) && Objects.equals(family, plant.family) && Objects.equals(taxonId, plant.taxonId) && Objects.equals(content, plant.content) && Objects.equals(createdTime, plant.createdTime) && Objects.equals(mainPhotoName, plant.mainPhotoName) && Objects.equals(createdAt, plant.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantId, createdBy, title, family, taxonId, heightFrom, heightTo, content, createdTime, mainPhotoName, category, createdInCloud, deleted, syncedWithCloud, bloomsMonth1, bloomsMonth2, bloomsMonth3, bloomsMonth4, bloomsMonth5, bloomsMonth6, bloomsMonth7, bloomsMonth8, bloomsMonth9, bloomsMonth10, bloomsMonth11, bloomsMonth12, createdAt);
    }
}
