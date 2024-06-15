package com.susarne.ihave2.persistence;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.System;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlantRepository {
    private static final String TAG = "PlantRepository";
    private final PlantDatabase mPlantDatabase;

    // Plant
    public PlantRepository(Context context) {
        Log.d(TAG, "PlantRepository: ");
        mPlantDatabase = PlantDatabase.getInstance(context);
    }

    public List<String> getAllTableNames() {
        return mPlantDatabase.getPlantDao().getAllTableNames();
    }


    public Completable deletePlant(Plant plant) {
        return mPlantDatabase.getPlantDao().deletePlant(plant);
    }
    public void insertSystem(System system) {
        mPlantDatabase.getPlantDao().insertSystem(system);
    }

    public void updateSystem(System system) {
        mPlantDatabase.getPlantDao().updateSystem(system);
    }



    public LiveData<PlantWithLists> retrievePlantById(String id){
        Log.d(TAG, "retrievePlantById: ");
        return mPlantDatabase.getPlantDao().getPlant(id);
    }

    public PlantWithLists retrievePlantByIdSync(String id){
        Log.d(TAG, "retrievePlantById: ");
        return mPlantDatabase.getPlantDao().getPlantSync(id);
    }
    public System retrieveSystemByIdSync(int id) {
        return mPlantDatabase.getPlantDao().getSystemSync(id);
    }
    public PlantWithLists getNotUploadedPlant() {
        return mPlantDatabase.getPlantDao().getNotUploadedPlantSync();
    }

    public PlantWithLists getFirstPlant() {
        return mPlantDatabase.getPlantDao().getFirstPlant();
    }
    public int markPlantAsUploaded(String plantId) {
        Log.d(TAG, "markPlantAsUploaded: "+plantId);
        return mPlantDatabase.getPlantDao().markPlantAsUploaded(plantId);
    }

    public LiveData<List<PlantWithLists>> retrievePlantsTask(){
        Log.d(TAG, "retrievePlantsTask: ");
        return mPlantDatabase.getPlantDao().getPlants();
    }

    public LiveData<List<PlantWithLists>> retrievePlantsWithLen3(){
        return mPlantDatabase.getPlantDao().getPlantsWithLen3();
    }

    public LiveData<List<PlantWithLists>> retrievePlantsTask2(String s){
        Log.d(TAG, "retrievePlantsTask: ");
        return mPlantDatabase.getPlantDao().getPlantsWithTitle(s);
    }





    public void insertPlantWithLists(PlantWithLists plantWithLists){
        Log.d(TAG, "insertPlantwithlist: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().insertPlantWithLists(plantWithLists))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plant ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: insertplantwithlist ");
                        e.printStackTrace();
                    }
                });

    }

    public void insertPlant(Plant plant){
        Log.d(TAG, "insertPlant: ");
        Log.d(TAG, "insertPlant: plant.toSstring"+plant.toString());
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().insertPlant(plant))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plant ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: insertplant ");
                        e.printStackTrace();
                    }
                });

    }

    public void insertPlantSync(Plant plant){
        Log.d(TAG, "insertPlant: ");
        Log.d(TAG, "insertPlant: plant.toSstring"+plant.toString());
        mPlantDatabase.getPlantDao().insertPlant(plant);

    }

    public void insertPlantPhoto(PlantPhoto plantPhoto){
        Log.d(TAG, "insertPlantPhoto: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().insertPlantPhoto(plantPhoto))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plantphoto "+plantPhoto.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: insertplantphoto ");
                        e.printStackTrace();


                    }
                });

    }




    public void updatePlantPhoto(PlantPhoto plantPhoto){
        Log.d(TAG, "insertPlantFloweMonth: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().updatePlantPhoto(plantPhoto))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plantphoto "+plantPhoto.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: updateplantPhoto ");
                        e.printStackTrace();
                    }
                });

    }



    public void updatePlant(Plant plant){
        Log.d(TAG, "updatePlant: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().updatePlant(plant))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete:plant ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: updateplant ");
                        e.printStackTrace();

                    }
                });

    }


    public int updatePlantPhotoSync(PlantPhoto plantPhoto) {
        Log.d(TAG, "updatePlantPhoto: #a "+plantPhoto.toString());
        return mPlantDatabase.getPlantDao().updatePlantPhoto(plantPhoto);
    }
    public void deleteAllPlants() {
        mPlantDatabase.getPlantDao().deleteAllPlantPhotos();
        mPlantDatabase.getPlantDao().deleteAllPlants();
    }

    public void deleteSystem() {
        mPlantDatabase.getPlantDao().deleteSystem();
    }

    public void upsertPlantsWithsLists(List<PlantWithLists> plantsWithLists, System system) {
        mPlantDatabase.getPlantDao().upsertPlantsWithLists(plantsWithLists,system);
    }

    public void upsertPlant(Plant plant) {
        mPlantDatabase.getPlantDao().upsertPlant(plant);
    }

    public void upsertPlantPhoto(PlantPhoto plantPhoto) {
        mPlantDatabase.getPlantDao().upsertPlantPhoto(plantPhoto);
    }

    public void upsertPlantPhotos(List<PlantPhoto> plantPhotos) {
        mPlantDatabase.getPlantDao().upsertPlantPhotos(plantPhotos);
    }

    public void setLastGetUpdatedPlantsUntil(String lastGetUpdatedPlantsUntil){
        mPlantDatabase.getPlantDao().setLastGetUpdatedPlantsUntil(lastGetUpdatedPlantsUntil);
    }


}
