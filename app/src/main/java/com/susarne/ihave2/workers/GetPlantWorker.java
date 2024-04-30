package com.susarne.ihave2.workers;

import static com.susarne.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.susarne.ihave2.util.Constants.WORKER_PLANT_ID;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.models.GooglePhotos.MediaItem;
import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantFlowerMonth;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.models.PlantsWithListsDto;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.util.ContextSingleton;
import com.susarne.ihave2.util.CurrentUser;
import com.susarne.ihave2.util.GooglePhoto;
import com.susarne.ihave2.util.Token;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class GetPlantWorker extends Worker {
    private static final String TAG = "GetPlantWorker";
    private static final String APP_TAG = "MyCustomApp";
    private PlantRepository mPlantRepository;
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;
    private File mMediaStorageDir, mImageFile;

    public GetPlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        int workerPlantId = getInputData().getInt(WORKER_PLANT_ID, 0);
        mContext = ContextSingleton.getContekst();
        mAccessTokenString = Token.getAccessToken(ACCESS_TOKEN_PHOTO);

        if (getFirstPlant() == null) {
            GooglePhoto.createPhotoDir(getApplicationContext());
            PlantsWithListsDto plantsWithListsDto = getPlantsForUserId(CurrentUser.getUserId());
            Log.d(TAG, "doWork: plantsWithListsDto: " + plantsWithListsDto + "/" + plantsWithListsDto.toString());
            for (PlantWithListsDto plantWithListsDto : plantsWithListsDto.plants) {
                for (PlantPhoto plantphoto : plantWithListsDto.getPlantPhotos()) {
                    Log.d(TAG, "doWork: " + plantphoto.getUploadedPhotoReference());
                    MediaItem mediaItem = GooglePhoto.getMediaItem(plantphoto.getUploadedPhotoReference());
                    if (mediaItem != null) {
                        GooglePhoto.downloadImage(mediaItem.getBaseUrl(), plantphoto.getPhotoName(), getApplicationContext());
                    }
                }
                insertPlant(plantWithListsDto);


            }

        }
        return Result.success();
    }

    private void insertPlant(PlantWithListsDto plantWithListsDto) {
        Plant plant = new Plant();

        plant.setPlantId(plantWithListsDto.getPlantId());
        plant.setUserId(plantWithListsDto.getUserId());
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

        for (PlantFlowerMonth p : plantWithListsDto.getPlantFlowerMonths()) {
            PlantFlowerMonth plantFlowerMonth = new PlantFlowerMonth(p);
            plantFlowerMonth.setCreatedInCloud(true);
            plantFlowerMonth.setSyncedWithCloud(true);
            mPlantRepository.insertPlantFlowerMonth(plantFlowerMonth);
        }


    }


    private PlantsWithListsDto getPlantsForUserId(int userId) {
        //her skal vi kalde retrofit for at uploade synkront
        Call<PlantsWithListsDto> call = PlantApiClient.getInstance().getMyApi().getPlantsForUserId(userId);
        try {
            Response<PlantsWithListsDto> response = call.execute();
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
