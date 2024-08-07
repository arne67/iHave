package com.susarne.ihave2.persistence;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Upsert;

import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.System;
import com.susarne.ihave2.models.Taxon;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public abstract class PlantDao {
    //Plant
    private static final String TAG = "PlantDao";
    @Insert
    public abstract void insertPlant(Plant plant);
    @Upsert
    public abstract void upsertPlant(Plant plant);

    @Update
    abstract void updatePlant(Plant plant);

    @Update
    abstract int updatePlantPhoto(PlantPhoto plantPhoto);
    @Upsert
    abstract void upsertPlantPhoto(PlantPhoto plantPhoto);
    @Insert
    public abstract void insertPlantPhotos(List<PlantPhoto> plantPhotos);
    @Upsert
    public abstract void upsertPlantPhotos(List<PlantPhoto> plantPhotos);

    @Update
    abstract int updateSystem(System system);



    @Insert
    public abstract void insertPlantPhoto(PlantPhoto plantPhoto);

    @Delete
    abstract Completable deletePlant(Plant plant);

    @Query("SELECT * FROM plants where plantId=:id" )
    public abstract LiveData<PlantWithLists> getPlant(String id);

    @Query("SELECT * FROM plants where plantId=:id")
    public abstract PlantWithLists getPlantSync(String id);

    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    //public abstract PlantWithLists getNotUploadedPlantSync();
    @Query("SELECT * FROM plants p where p.syncedWithCloud=0 or exists (select 'x' from plantPhotos pp where pp.plantId = p.plantId and pp.syncedWithCloud=0)  order by p.plantId desc limit 1")
    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    public abstract PlantWithLists getNotUploadedPlantSync();

    @Query("SELECT * FROM plants p limit 1")
    //@Query("SELECT * FROM plants p where p.syncedWithCloud=0 limit 1")
    public abstract PlantWithLists getFirstPlant();

    @Query("update plants set syncedWithCloud=1, createdInCloud=1 where plantId=:plantId")
    public abstract int markPlantAsUploaded(String plantId);
    @Transaction
    @Query("SELECT * FROM plants where deleted=0 order by plantId")
    public abstract LiveData<List<PlantWithLists>> getPlants();

    @Transaction
    @Query("SELECT * FROM plants where deleted=0 and title like :s order by plantId")
    public abstract LiveData<List<PlantWithLists>> getPlantsWithTitle(String s);


    @Transaction
    @Query("SELECT danskNavn FROM Taxon where taxonRang=:taxonRang and danskNavn like :danskNavn order by danskNavn")
    public abstract LiveData<List<String>> getTaxonsWithName(String taxonRang,String danskNavn);

    @Transaction
    @Query("SELECT * FROM plants where deleted=0 and length(title) = 3")
    public abstract LiveData<List<PlantWithLists>> getPlantsWithLen3();


    @Query("SELECT name FROM sqlite_master WHERE type='table'")
    public abstract List<String> getAllTableNames();

    //PlantFLowMonth


    @Transaction
    public void deleteAndCreatePlantPhotos(String plantId, List<PlantPhoto> plantPhotos)
            throws InterruptedException {
        Log.d("PlantDato", "deleteAndCreate: ");
        Log.d("PlantDato", "deleteAndCreate: om 5 sek gemmer vi ");
        //Thread.sleep(1);
        Log.d("PlantDato", "deleteAndCreate: nu gemmer vi ");

        deletePhotosForPlant(plantId);
        insertPlantPhotos(plantPhotos);
    }

    @Transaction
    public void upsertPlantsWithLists(List<PlantWithLists> plantsWithLists, System system) {

        for (PlantWithLists p: plantsWithLists) {
            upsertPlant(p.plant);
            upsertPlantPhotos(p.plantPhotos);
        }
        updateSystem(system);
    }


    @Transaction
    public void insertPlantWithLists(PlantWithLists plantWithLists)
            throws InterruptedException {

        insertPlant(plantWithLists.plant);
        insertPlantPhotos(plantWithLists.plantPhotos);

    }

    @Transaction
    public void updatePlantWithLists(PlantWithLists plantWithLists)
            throws InterruptedException {
        Log.d(TAG, "updatePlantWithLists: "+plantWithLists.plant.getTitle());
        updatePlant(plantWithLists.plant);


        deletePhotosForPlant(plantWithLists.plant.getPlantId());
        insertPlantPhotos(plantWithLists.plantPhotos);


    }





    @Query("DELETE FROM plantPhotos where plantId = :plantId")
    public abstract int deletePhotosForPlant(String plantId);

    @Insert
    public abstract void insertSystem(System system);

    @Query("SELECT * FROM system where systemId=:id")
    public abstract System getSystemSync(int id);
    @Query("DELETE FROM plantPhotos")
    public abstract int deleteAllPlantPhotos();
    @Query("DELETE FROM plants")
    public abstract int deleteAllPlants();
    @Query("DELETE FROM system")
    public abstract int deleteSystem();

    @Query("update system set lastGetUpdatedPlantsUntil=:lastGetUpdatedPlantsUntil where systemId=0")
    public abstract int setLastGetUpdatedPlantsUntil(String lastGetUpdatedPlantsUntil);

    @Query("update system set lastGetUpdatedTaxonsUntil=:lastGetUpdatedTaxonsUntil where systemId=0")
    public abstract int setLastGetUpdatedTaxonsUntil(String lastGetUpdatedTaxonsUntil);

    @Upsert
    public abstract void upsertTaxon(Taxon taxon);

    @Query("SELECT fam.danskNavn "+
            "FROM Taxon as spec "+
            "JOIN Taxon as gen "+
            "  ON gen.taxonRang='Slægt' "+
            " AND gen.taxonId = spec.parentTaxonid "+
            "JOIN Taxon as fam "+
            "  ON fam.taxonRang='Familie' "+
            " AND fam.taxonId = gen.parentTaxonid "+
            "where spec.taxonRang='Art' and spec.taxonId = :taxonId")
    public abstract LiveData<String> getFamilyForSpecies(String taxonId);

    @Query("SELECT * "+
            "FROM Taxon as spec "+
            "where spec.taxonRang=:taxonRang and spec.danskNavn = :danskNavn ")
    public abstract LiveData<Taxon> getTaxonWithName(String taxonRang, String danskNavn);

    @Query("SELECT * "+
            "FROM Taxon as spec "+
            "where spec.taxonId = :taxonId ")
    public abstract LiveData<Taxon> getTaxonById(String taxonId);

    @Query("WITH RECURSIVE TaxonHierarchy(taxonId, taxonParentId, combNavn,  depth) AS ("+
    "SELECT taxonId, parentTaxonid, taxonRang||'#'||danskNavn||'#'||videnskabeligtNavn as combNavn, 0 AS depth "+
    "FROM Taxon "+
    "WHERE taxonId = :taxonId "+
    "UNION ALL "+
    "SELECT t.taxonId, t.parentTaxonid, t.taxonRang||'#'||t.danskNavn||'#'||t.videnskabeligtNavn, th.depth + 1 "+
    "FROM Taxon t "+
    "INNER JOIN TaxonHierarchy th ON t.taxonId = th.taxonParentId "+
    ") "+
//    "SELECT * FROM TaxonHierarchy ORDER BY depth DESC")
    "SELECT combNavn FROM TaxonHierarchy ORDER BY depth DESC")
    public abstract LiveData<List<String>> getTaxonhierarchy(String taxonId);

}
