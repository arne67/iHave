package com.susarne.ihave2.util;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.susarne.ihave2.app.MyApplication;
import com.susarne.ihave2.workers.GetUpdatedPlantWorker;

public class Worker {

    private static WorkManager mWorkManager;

    public static void getNewUpdatedPlants() {
        mWorkManager = WorkManager.getInstance(MyApplication.getAppContext());
        OneTimeWorkRequest getPlantRequest =
                new OneTimeWorkRequest.Builder(GetUpdatedPlantWorker.class)
                        .build();
        mWorkManager.enqueueUniqueWork("GetUpdatedPlantWorker", ExistingWorkPolicy.APPEND, getPlantRequest);

    }
}
