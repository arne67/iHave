package com.susarne.ihave2.util;

import static com.susarne.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.susarne.ihave2.util.Constants.WORKER_PLANT_ID;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.app.MyApplication;
import com.susarne.ihave2.models.GetUpdatedPlantsDto;
import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.models.System;
import com.susarne.ihave2.persistence.PlantRepository;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class PlantUpdater {
    private static final String TAG = "PlantUpdater";
    private static final String APP_TAG = "MyCustomApp";
    private PlantRepository mPlantRepository;
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;
    private File mMediaStorageDir, mImageFile;

    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;




    public void getUpdatedPlants() {
        Log.d(TAG, "GetUpdatedPlants() 1: ");

        mContext = MyApplication.getAppContext();
        mPlantRepository = new PlantRepository(mContext);
        mAccessTokenString = Token.getAccessToken(ACCESS_TOKEN_PHOTO);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        GooglePhoto.createPhotoDir(mContext);
        Log.d(TAG, "getUpdatedPlants: getLastGetUpdatedPlantsUntil(): " + getLastGetUpdatedPlantsUntil());
        GetUpdatedPlantsDto getUpdatedPlantsDto = getUpdatedPlants(getLastGetUpdatedPlantsUntil());

        if (getUpdatedPlantsDto != null) {
            for (Plant plant : getUpdatedPlantsDto.getPlants()) {
                upsertPlant(plant);
            }
            for (PlantPhoto plantPhoto : getUpdatedPlantsDto.getPlantPhotos()) {
                upsertPlantPhoto(plantPhoto);
            }
            for (PlantPhoto plantPhoto : getUpdatedPlantsDto.getPlantPhotos()) {
                downloadImage(plantPhoto);
            }
        }

        mPlantRepository.setLastGetUpdatedPlantsUntil(getUpdatedPlantsDto.getUpdatedUntil());


        return;
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
            //return "1900-01-01 00:00:00.000000";
            return system.getLastGetUpdatedPlantsUntil();
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

        //Call<GetUpdatedPlantsDto> call = PlantApiClient.getInstance().getMyApi().getUpdatedPlants(CurrentUser.getUserId(), lastGetUpdatedPlantsUntil);
        Call<GetUpdatedPlantsDto> call = PlantApiClient.getInstance().getMyApi().getUpdatedPlants(mAuth.getCurrentUser().getUid(), lastGetUpdatedPlantsUntil);
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

    private void downloadImage(PlantPhoto plantPhoto) {
        Log.d(TAG, "downloadImage: plantphoto.getPhotoName(): "+plantPhoto.getPhotoName());

        File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        File localFile = new File(mediaStorageDir.getPath() + File.separator + plantPhoto.getPhotoName());

        StorageReference childStorageRef = mStorageRef.child("images/" + plantPhoto.getPhotoName());

        //mStorageRef.child("images/" + plantPhoto.getPhotoName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        childStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: vi har hentet image ved firebase");
                // Handle the URI, e.g., load it into an ImageView using a library like Glide or Picasso
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });
    }


}
