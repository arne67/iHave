package com.example.ihave2.persistence;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.ihave2.models.Plant;
import com.example.ihave2.models.PlantFlowerMonth;
import com.example.ihave2.models.PlantPhoto;
import com.example.ihave2.models.PlantWithLists;
import com.example.ihave2.models.System;

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


    public LiveData<PlantWithLists> retrievePlantById(int id){
        Log.d(TAG, "retrievePlantById: ");
        return mPlantDatabase.getPlantDao().getPlant(id);
    }

    public PlantWithLists retrievePlantByIdSync(int id){
        Log.d(TAG, "retrievePlantById: ");
        return mPlantDatabase.getPlantDao().getPlantSync(id);
    }
    public System retrieveSystemByIdSync(int id) {
        return mPlantDatabase.getPlantDao().getSystemSync(id);
    }
    public PlantWithLists getNotUploadedPlant() {
        return mPlantDatabase.getPlantDao().getNotUploadedPlantSync();
    }
    public int markPlantAsUploaded(int plantId) {
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
    public LiveData<Integer> getMaxPlantId(){
        return mPlantDatabase.getPlantDao().getMaxPlantId();
    }




    public void insertPlantWithLists(PlantWithLists plantWithLists){
        Log.d(TAG, "insertPlant: ");
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
                        Log.d(TAG, "onError: plant ");

                    }
                });

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
                        Log.d(TAG, "onError: plantphoto ");

                    }
                });

    }

    public void insertPlantFlowerMonth(PlantFlowerMonth plantFlowerMonth){
        Log.d(TAG, "insertPlantFloweMonth: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().insertPlantFlowerMonth(plantFlowerMonth))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plantphoto "+plantFlowerMonth.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: plantFlowerMonth ");

                    }
                });

    }


    public void updatePlantFlowerMonth(PlantFlowerMonth plantFlowerMonth){
        Log.d(TAG, "insertPlantFloweMonth: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().updatePlantFlowerMonth(plantFlowerMonth))
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                // execute this RxJava
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete:plantphoto "+plantFlowerMonth.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: plantFlowerMonth ");

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
                        Log.d(TAG, "onError: plantPhoto ");

                    }
                });

    }


    public void updatePlantWithLists(PlantWithLists plantWithLists){
        Log.d(TAG, "insertPlant: ");
        Completable.fromAction(() -> mPlantDatabase.getPlantDao().updatePlantWithLists(plantWithLists))
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
                        Log.d(TAG, "onError: plant ");

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
                        Log.d(TAG, "onError: plant ");

                    }
                });

    }

    public int updatePlantFlowerMonthSync(PlantFlowerMonth plantFlowerMonth) {
        Log.d(TAG, "updatePlantFlowerMonthSync: #a "+plantFlowerMonth.toString());
        return mPlantDatabase.getPlantDao().updatePlantFlowerMonth(plantFlowerMonth);
    }
    public int updatePlantPhotoSync(PlantPhoto plantPhoto) {
        Log.d(TAG, "updatePlantPhoto: #a "+plantPhoto.toString());
        return mPlantDatabase.getPlantDao().updatePlantPhoto(plantPhoto);
    }
    public void deleteAllPlants() {
        mPlantDatabase.getPlantDao().deleteAllPlantPhotos();
        mPlantDatabase.getPlantDao().deleteAllPlantFlowerMonths();
        mPlantDatabase.getPlantDao().deleteAllPlants();
    }

}
