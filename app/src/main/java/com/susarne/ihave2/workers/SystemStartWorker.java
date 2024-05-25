package com.susarne.ihave2.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.susarne.ihave2.models.System;
import com.susarne.ihave2.persistence.PlantRepository;

public class SystemStartWorker extends Worker {
    private static final String TAG = "SystemStartWorker";
    private PlantRepository mPlantRepository;

    public SystemStartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
         mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (systemExist() == false) createSystemSync();
        return Result.success();
    }



    private boolean systemExist() {
        System system=mPlantRepository.retrieveSystemByIdSync(1);
        if (system!=null) return true;
        return false;

    }
    private void createSystemSync() {
        System system=new System();
        system.setSystemId(1);
        system.setUserId("");
        system.setLastUsedId(0);
        mPlantRepository.insertSystem(system);
    }

}
