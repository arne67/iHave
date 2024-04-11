package com.example.ihave2.workers;

import static com.example.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.example.ihave2.util.Constants.DELAY_TIME_MILLIS;
import static com.example.ihave2.util.Constants.WORKER_PLANT_ID;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ihave2.api.GooglePhotoApiClient;
import com.example.ihave2.api.GooglePhotoApiScalarsClient;
import com.example.ihave2.api.PlantApiClient;
import com.example.ihave2.models.GetUserRespondDto;
import com.example.ihave2.models.GooglePhotos.Album;
import com.example.ihave2.models.GooglePhotos.AlbumsCreateRequestBody;
import com.example.ihave2.models.GooglePhotos.BatchCreateRequestBody;
import com.example.ihave2.models.GooglePhotos.BatchCreateRespondBody;
import com.example.ihave2.models.GooglePhotos.MediaItem;
import com.example.ihave2.models.GooglePhotos.NewMediaItem;
import com.example.ihave2.models.GooglePhotos.NewMediaItemResult;
import com.example.ihave2.models.GooglePhotos.SimpleMediaItem;
import com.example.ihave2.models.PhotoAlbumIdDto;
import com.example.ihave2.models.Plant;
import com.example.ihave2.models.PlantDto;
import com.example.ihave2.models.PlantFlowerMonth;
import com.example.ihave2.models.PlantFlowerMonthDto;
import com.example.ihave2.models.PlantPhoto;
import com.example.ihave2.models.PlantPhotoDto;
import com.example.ihave2.models.PlantWithLists;
import com.example.ihave2.models.PlantWithListsDto;
import com.example.ihave2.models.PlantsWithListsDto;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.util.ContextSingleton;
import com.example.ihave2.util.CurrentUser;
import com.example.ihave2.util.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

        if (getFirstPlant()==null) {
            PlantsWithListsDto plantsWithListsDto = getPlantsForUserId(CurrentUser.getUserId());
            Log.d(TAG, "doWork: plantsWithListsDto: "+plantsWithListsDto+"/"+plantsWithListsDto.toString());
            for (PlantWithListsDto plantWithListsDto:plantsWithListsDto.plants) {
                insertPlant(plantWithListsDto);

            }

        }
        return Result.success();
    }

    private void insertPlant(PlantWithListsDto plantWithListsDto) {
        PlantWithLists plantWithLists = new PlantWithLists();
        plantWithLists.plant = new Plant();

        plantWithLists.plant.setPlantId(plantWithListsDto.getPlantId());
        plantWithLists.plant.setUserId(plantWithListsDto.getUserId());
        plantWithLists.plant.setContent(plantWithListsDto.getContent());
        plantWithLists.plant.setCreatedTime(plantWithListsDto.getCreatedTime());
        plantWithLists.plant.setTitle(plantWithListsDto.getTitle());
        plantWithLists.plant.setCategory(plantWithListsDto.getCategory());
        plantWithLists.plant.setDeleted(plantWithListsDto.isDeleted());
        plantWithLists.plant.setMainPhotoName(plantWithListsDto.getMainPhotoName());
        plantWithLists.plant.setCreatedInCloud(true);
        plantWithLists.plant.setSyncedWithCloud(true);


        plantWithLists.plantPhotos=plantWithListsDto.getPlantPhotos();
        plantWithLists.plantFlowerMonths=plantWithListsDto.getPlantFlowerMonths();
    mPlantRepository.insertPlantWithLists(plantWithLists);
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
