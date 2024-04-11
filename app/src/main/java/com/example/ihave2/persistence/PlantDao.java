package com.example.ihave2.persistence;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.ihave2.models.Plant;
import com.example.ihave2.models.PlantFlowerMonth;
import com.example.ihave2.models.PlantPhoto;
import com.example.ihave2.models.PlantWithLists;
import com.example.ihave2.models.System;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public abstract class PlantDao {
    //Plant
    private static final String TAG = "PlantDao";
    @Insert
    public abstract void insertPlant(Plant plant);

    @Update
    abstract void updatePlant(Plant plant);

    @Update
    abstract int updatePlantPhoto(PlantPhoto plantPhoto);

    @Update
    abstract int updatePlantFlowerMonth(PlantFlowerMonth plantFlowerMonth);

    @Insert
    public abstract void insertPlantPhoto(PlantPhoto plantPhoto);

    @Insert
    public abstract void insertPlantFlowerMonth(PlantFlowerMonth plantFlowerMonth);

    @Delete
    abstract Completable deletePlant(Plant plant);


    @Query("SELECT * FROM plants where plantId=:id")
    public abstract LiveData<PlantWithLists> getPlant(int id);

    @Query("SELECT * FROM plants where plantId=:id")
    public abstract PlantWithLists getPlantSync(int id);

    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    //public abstract PlantWithLists getNotUploadedPlantSync();
    @Query("SELECT * FROM plants p where p.syncedWithCloud=0 or exists (select 'x' from plantPhotos pp where pp.plantId = p.plantId and pp.syncedWithCloud=0) or exists (select 'x' from plantFlowerMonths pf where pf.plantId = p.plantId and pf.syncedWithCloud=0) order by p.plantId desc limit 1")
    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    public abstract PlantWithLists getNotUploadedPlantSync();

    @Query("SELECT * FROM plants p limit 1")
    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    public abstract PlantWithLists getFirstPlant();

    @Query("update plants set syncedWithCloud=1, createdInCloud=1 where plantId=:plantId")
    public abstract int markPlantAsUploaded(int plantId);
    @Transaction
    @Query("SELECT * FROM plants where deleted=0 order by plantId")
    public abstract LiveData<List<PlantWithLists>> getPlants();

    @Transaction
    @Query("SELECT * FROM plants where deleted=0 and title like :s order by plantId")
    public abstract LiveData<List<PlantWithLists>> getPlantsWithTitle(String s);

    @Transaction
    @Query("SELECT * FROM plants where deleted=0 and length(title) = 3")
    public abstract LiveData<List<PlantWithLists>> getPlantsWithLen3();


    @Query("SELECT max(plantId) FROM plants")
    public abstract LiveData<Integer> getMaxPlantId();

    @Query("SELECT name FROM sqlite_master WHERE type='table'")
    public abstract List<String> getAllTableNames();

    //PlantFLowMonth

    @Transaction
    public void deleteAndCreatePlantFlowerMonths(int plantId, List<PlantFlowerMonth> plantFlowerMonths)
            throws InterruptedException {
        Log.d("PlantDato", "deleteAndCreate: ");
        Log.d("PlantDato", "deleteAndCreate: om 5 sek gemmer vi ");
        //Thread.sleep(1);
        Log.d("PlantDato", "deleteAndCreate: nu sek gemmer vi ");

        deleteFlowerMonthsForPlant(plantId);
        insertPlantFlowerMonths(plantFlowerMonths);
    }

    @Transaction
    public void deleteAndCreatePlantPhotos(int plantId, List<PlantPhoto> plantPhotos)
            throws InterruptedException {
        Log.d("PlantDato", "deleteAndCreate: ");
        Log.d("PlantDato", "deleteAndCreate: om 5 sek gemmer vi ");
        //Thread.sleep(1);
        Log.d("PlantDato", "deleteAndCreate: nu gemmer vi ");

        deletePhotosForPlant(plantId);
        insertPlantPhotos(plantPhotos);
    }

    @Transaction
    public void insertPlantWithLists(PlantWithLists plantWithLists)
            throws InterruptedException {

        insertPlant(plantWithLists.plant);
        insertPlantFlowerMonths(plantWithLists.plantFlowerMonths);
        insertPlantPhotos(plantWithLists.plantPhotos);

    }

    @Transaction
    public void updatePlantWithLists(PlantWithLists plantWithLists)
            throws InterruptedException {
        Log.d(TAG, "updatePlantWithLists: "+plantWithLists.plant.getTitle());
        updatePlant(plantWithLists.plant);

        deleteFlowerMonthsForPlant(plantWithLists.plant.getPlantId());
        insertPlantFlowerMonths(plantWithLists.plantFlowerMonths);

        deletePhotosForPlant(plantWithLists.plant.getPlantId());
        insertPlantPhotos(plantWithLists.plantPhotos);


    }


    @Query("DELETE FROM plantFlowerMonths where plantId = :plantId")
    public abstract int deleteFlowerMonthsForPlant(int plantId);

    @Insert
    public abstract void insertPlantFlowerMonths(List<PlantFlowerMonth> plantFlowerMonths);

    @Query("DELETE FROM plantPhotos where plantId = :plantId")
    public abstract int deletePhotosForPlant(int plantId);

    @Insert
    public abstract void insertPlantPhotos(List<PlantPhoto> plantPhotos);

    @Insert
    public abstract void insertSystem(System system);

    @Query("SELECT * FROM system where systemId=:id")
    public abstract System getSystemSync(int id);
    @Query("DELETE FROM plantPhotos")
    public abstract int deleteAllPlantPhotos();
    @Query("DELETE FROM plantFlowerMonths")
    public abstract int deleteAllPlantFlowerMonths();
    @Query("DELETE FROM plants")
    public abstract int deleteAllPlants();


}
