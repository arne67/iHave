package com.susarne.ihave2.workers;

import static com.susarne.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.susarne.ihave2.util.Constants.WORKER_PLANT_ID;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.models.GetUpdatedPlantsDto;
import com.susarne.ihave2.models.GooglePhotos.MediaItem;
import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.models.System;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.util.ContextSingleton;
import com.susarne.ihave2.util.GooglePhoto;
import com.susarne.ihave2.util.Token;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class GetUpdatedPlantWorker extends Worker {
    private static final String TAG = "GetPlantWorker";
    private static final String APP_TAG = "MyCustomApp";
    private PlantRepository mPlantRepository;
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;
    private File mMediaStorageDir, mImageFile;

    public GetUpdatedPlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        int workerPlantId = getInputData().getInt(WORKER_PLANT_ID, 0);
        mContext = ContextSingleton.getContekst();
        mAccessTokenString = Token.getAccessToken(ACCESS_TOKEN_PHOTO);

        GooglePhoto.createPhotoDir(getApplicationContext());
        GetUpdatedPlantsDto getUpdatedPlantsDto = getUpdatedPlants(getLastGetUpdatedPlantsUntil());

        for (Plant plant : getUpdatedPlantsDto.getPlants()) {
            upsertPlant(plant);
        }
        for (PlantPhoto plantPhoto : getUpdatedPlantsDto.getPlantPhotos()) {
            MediaItem mediaItem = GooglePhoto.getMediaItem(plantPhoto.getUploadedPhotoReference());
            if (mediaItem != null) {
                GooglePhoto.downloadImage(mediaItem.getBaseUrl(), plantPhoto.getPhotoName(), getApplicationContext());
            }
            upsertPlantPhoto(plantPhoto);
        }
        mPlantRepository.setLastGetUpdatedPlantsUntil(getUpdatedPlantsDto.getUpdatedUntil());


        return Result.success();
    }

    private void upsertPlantPhoto(PlantPhoto plantPhoto) {
        plantPhoto.setCreatedInCloud(true);
        plantPhoto.setSyncedWithCloud(true);
        mPlantRepository.upsertPlantPhoto(plantPhoto);
    }

    private void upsertPlant(Plant plant) {
        plant.setCreatedInCloud(true);
        plant.setSyncedWithCloud(true);
        mPlantRepository.upsertPlant(plant);
    }

    private String getLastGetUpdatedPlantsUntil() {
        System system = mPlantRepository.retrieveSystemByIdSync(0);
        if (system == null) {
            return "1900-01-01 00:00:00.000000";

        } else {
            return "1900-01-01 00:00:00.000000";
            //return system.getLastGetUpdatedPlantsUntil();
        }


    }

    private void insertPlant(PlantWithListsDto plantWithListsDto) {
        Plant plant = new Plant();

        plant.setPlantId(plantWithListsDto.getPlantId());
        plant.setCreatedBy(plantWithListsDto.getCreatedBy());
        plant.setContent(plantWithListsDto.getContent());
        plant.setCreatedTime(plantWithListsDto.getCreatedTime());
        plant.setTitle(plantWithListsDto.getTitle());
        plant.setCategory(plantWithListsDto.getCategory());
        plant.setDeleted(plantWithListsDto.isDeleted());
        plant.setMainPhotoName(plantWithListsDto.getMainPhotoName());
        plant.setCreatedInCloud(true);
        plant.setSyncedWithCloud(true);
        mPlantRepository.insertPlant(plant);

        for (PlantPhoto p : plantWithListsDto.getPlantPhotos()) {
            PlantPhoto plantPhoto = new PlantPhoto(p);
            plantPhoto.setCreatedInCloud(true);
            plantPhoto.setSyncedWithCloud(true);
            mPlantRepository.insertPlantPhoto(plantPhoto);
        }

    }


    private GetUpdatedPlantsDto getUpdatedPlants(String lastGetUpdatedPlantsUntil) {
        //her skal vi kalde retrofit for at uploade synkront
        //Call<GetUpdatedPlantsDto> call = PlantApiClient.getInstance().getMyApi().getUpdatedPlants(CurrentUser.getUserId(), lastGetUpdatedPlantsUntil);
        Call<GetUpdatedPlantsDto> call = PlantApiClient.getInstance().getMyApi().getUpdatedPlants(1, lastGetUpdatedPlantsUntil);
        try {
            Response<GetUpdatedPlantsDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                return response.body();
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
        return null;

    }

    private PlantWithLists getFirstPlant() {
        PlantWithLists plantWithLists = mPlantRepository.getFirstPlant();
        return plantWithLists;
    }


}
