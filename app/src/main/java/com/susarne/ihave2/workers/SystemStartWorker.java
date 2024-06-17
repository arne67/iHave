package com.susarne.ihave2.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.susarne.ihave2.models.System;
import com.susarne.ihave2.persistence.PlantRepository;

public class SystemStartWorker extends Worker {
    private static final String TAG = "SystemStartWorker";
    private PlantRepository mPlantRepository;
    System mSystem;

    public SystemStartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
         mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: ");
        if (systemExist() == false) {
            createSystemSync();
            startGetPlantWorker();
        } else
            if (mSystem.getLastGetUpdatedPlantsUntil()==null) {
                mPlantRepository.setLastGetUpdatedPlantsUntil("1900-01-01 00:00:00.000000");
        }
        return Result.success();
    }



    private boolean systemExist() {
        mSystem=mPlantRepository.retrieveSystemByIdSync(0);
        if (mSystem!=null) return true;
        return false;

    }
    private void createSystemSync() {
        System system=new System();
        system.setSystemId(0);
        system.setUserId("");
        system.setLastUsedId(0);
        system.setLastGetUpdatedPlantsUntil("1900-01-01 00:00:00.000000");
        mPlantRepository.insertSystem(system);
    }

    private void startGetPlantWorker() {
        WorkManager mWorkManager;
        mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest getPlantsRequest =
                new OneTimeWorkRequest.Builder(GetPlantWorker.class)
                        .build();
        mWorkManager.enqueue(getPlantsRequest);

    }

}
