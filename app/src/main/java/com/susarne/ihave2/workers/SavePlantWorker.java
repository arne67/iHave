package com.susarne.ihave2.workers;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.susarne.ihave2.util.Constants.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.app.MyApplication;
import com.susarne.ihave2.models.GooglePhotos.MediaItem;
import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantDto;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantPhotoDto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.util.Token;
import com.susarne.ihave2.util.CurrentUser;
import com.susarne.ihave2.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class SavePlantWorker extends Worker {
    private static final String TAG = "SavePlantWorker";
    private static final String APP_TAG = "MyCustomApp";
    private PlantRepository mPlantRepository;
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;
    private File mMediaStorageDir, mImageFile;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    public SavePlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        sleep();
        mContext = MyApplication.getAppContext();
        mAccessTokenString = Token.getAccessToken(ACCESS_TOKEN_PHOTO);

        int idx = 1;
        mSucces = true;
        PlantWithLists plantWithLists = new PlantWithLists();
        plantWithLists = getNotUploadedPlant();
        while (plantWithLists != null && idx < 5) {
            idx++;
            uploadPhotos(plantWithLists.plantPhotos);
            plantWithLists = getPlant(plantWithLists.plant.getPlantId());
            Log.d(TAG, "doWork: #a " + plantWithLists.toString());
            if (!plantWithLists.plant.isCreatedInCloud()) {
                uploadNewPlant(plantWithLists);
                if (mSucces)
                    markPlantWithListsAsUploaded(plantWithLists);
            } else {
                if (!plantWithLists.plant.isSyncedWithCloud()) {
                    uploadChangedPlant(plantWithLists.plant);
                    if (mSucces)
                        markPlantAsUploaded(plantWithLists.plant);
                }

                for (PlantPhoto p : plantWithLists.plantPhotos) {
                    if (!p.isCreatedInCloud()) {
                        uploadNewPlantPhoto(p);
                        if (mSucces)
                            markPlantPhotoAsUploaded(p);
                    } else {
                        if (!p.isSyncedWithCloud()) {
                            uploadChangedPlantPhoto(p);
                            if (mSucces)
                                markPlantPhotoAsUploaded(p);
                        }
                    }

                }

            }

            plantWithLists = getNotUploadedPlant();
        }


        return Result.success();
    }

    private PlantWithLists getNotUploadedPlant() {
        PlantWithLists plantWithLists = mPlantRepository.getNotUploadedPlant();
        //Log.d(TAG, "getNotUploadPlant: id/title: "+plantWithLists.plant.getPlantId()+"/"+plantWithLists.plant.getTitle());
        //Log.d(TAG, "getNotUploadedPlant: #a: "+plantWithLists.toString());
        return plantWithLists;
    }


    private int updatePlantPhoto(PlantPhoto plantPhoto) {
        return mPlantRepository.updatePlantPhotoSync(plantPhoto);
    }


    private void uploadNewPlant(PlantWithLists plant) {
        //her skal vi kalde retrofit for at uploade synkront
        mSucces = false;
        Log.d(TAG, "uploadNewPlant: #a" + plant.plant.getPlantId());
        PlantWithListsDto plantWithListsDto = getPlantWithlistsDto(plant);
        plantWithListsDto.setPlantPhotos(null);
        String token = CurrentUser.getAccessToken();
        token = "xxx";
        Log.d(TAG, "uploadNewPlant: a# plantwithlistsdto " + plantWithListsDto.toString());
        Call<PlantWithListsDto> call = PlantApiClient.getInstance().getMyApi().createPlant(token, plantWithListsDto);
        try {
            Response<PlantWithListsDto> response = call.execute();
            if (response.isSuccessful()) {
                mSucces = true;
                // handle successful response
                Log.d(TAG, "uploadPlant: #a " + response.body());
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: a# notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
        for (PlantPhoto p : plant.plantPhotos) {
            uploadNewPlantPhoto(p);

        }
    }

    private PlantWithListsDto getPlantWithlistsDto(PlantWithLists plant) {
        PlantWithListsDto plantWithListsDto = new PlantWithListsDto();

        plantWithListsDto.setCreatedBy(plant.plant.getCreatedBy());
        plantWithListsDto.setPlantId(plant.plant.getPlantId());
        plantWithListsDto.setTitle(plant.plant.getTitle());
        plantWithListsDto.setContent(plant.plant.getContent());
        plantWithListsDto.setCreatedTime(plant.plant.getCreatedTime());
        plantWithListsDto.setMainPhotoName(plant.plant.getMainPhotoName());
        plantWithListsDto.setCategory(plant.plant.getCategory());
        plantWithListsDto.setDeleted(plant.plant.isDeleted());
        plantWithListsDto.setBloomsMonth1(plant.plant.isBloomsMonth1());
        plantWithListsDto.setBloomsMonth2(plant.plant.isBloomsMonth2());
        plantWithListsDto.setBloomsMonth3(plant.plant.isBloomsMonth3());
        plantWithListsDto.setBloomsMonth4(plant.plant.isBloomsMonth4());
        plantWithListsDto.setBloomsMonth5(plant.plant.isBloomsMonth5());
        plantWithListsDto.setBloomsMonth6(plant.plant.isBloomsMonth6());
        plantWithListsDto.setBloomsMonth7(plant.plant.isBloomsMonth7());
        plantWithListsDto.setBloomsMonth8(plant.plant.isBloomsMonth8());
        plantWithListsDto.setBloomsMonth9(plant.plant.isBloomsMonth9());
        plantWithListsDto.setBloomsMonth10(plant.plant.isBloomsMonth10());
        plantWithListsDto.setBloomsMonth11(plant.plant.isBloomsMonth11());
        plantWithListsDto.setBloomsMonth12(plant.plant.isBloomsMonth12());

        plantWithListsDto.setPlantPhotos(plant.plantPhotos);
        return plantWithListsDto;
    }

    private void uploadChangedPlant(Plant plant) {
        //her skal vi kalde retrofit for at uploade synkront
        Log.d(TAG, "uploadchangedPlant: " + plant.toString());
        mSucces = false;
        PlantDto plantDto = getPlantDto(plant);
        String token = "";
        Log.d(TAG, "uploadchangedPlant content: " + plantDto.getContent());
        Call<PlantDto> call = PlantApiClient.getInstance().getMyApi().updatePlant(token, plantDto, mAuth.getCurrentUser().getUid(), plantDto.getPlantId());
        try {
            Response<PlantDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                mSucces = true;
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
    }

    private PlantDto getPlantDto(Plant plant) {
        PlantDto plantDto = new PlantDto();

        plantDto.setCreatedBy(plant.getCreatedBy());
        plantDto.setPlantId(plant.getPlantId());
        plantDto.setTitle(plant.getTitle());
        plantDto.setContent(plant.getContent());
        plantDto.setCreatedTime(plant.getCreatedTime());
        plantDto.setMainPhotoName(plant.getMainPhotoName());
        plantDto.setCategory(plant.getCategory());
        plantDto.setDeleted(plant.isDeleted());
        plantDto.setBloomsMonth1(plant.isBloomsMonth1());
        plantDto.setBloomsMonth2(plant.isBloomsMonth2());
        plantDto.setBloomsMonth3(plant.isBloomsMonth3());
        plantDto.setBloomsMonth4(plant.isBloomsMonth4());
        plantDto.setBloomsMonth5(plant.isBloomsMonth5());
        plantDto.setBloomsMonth6(plant.isBloomsMonth6());
        plantDto.setBloomsMonth7(plant.isBloomsMonth7());
        plantDto.setBloomsMonth8(plant.isBloomsMonth8());
        plantDto.setBloomsMonth9(plant.isBloomsMonth9());
        plantDto.setBloomsMonth10(plant.isBloomsMonth10());
        plantDto.setBloomsMonth11(plant.isBloomsMonth11());
        plantDto.setBloomsMonth12(plant.isBloomsMonth12());

        return plantDto;
    }


    private void uploadNewPlantPhoto(PlantPhoto plantPhoto) {
        //her skal vi kalde retrofit for at uploade synkront
        Log.d(TAG, "uploadPlantPhoto: ");
        mSucces = false;
        PlantPhotoDto plantPhotoDto = getPlantPhotoDto(plantPhoto);
        plantPhotoDto.setUploadedPhotoReference(plantPhotoDto.getPhotoName());

        String token = "";
        Call<PlantPhotoDto> call = PlantApiClient.getInstance().getMyApi().createPlantPhoto(token, plantPhotoDto);
        try {
            Response<PlantPhotoDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                mSucces = true;
                //uploadPhotos(plant.plantPhotos);
                //markPlantAsUploaded(response.body().getPlantId());
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlantPhoto: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
    }

    private void uploadChangedPlantPhoto(PlantPhoto plantPhoto) {
        //her skal vi kalde retrofit for at uploade synkront
        Log.d(TAG, "uploadPlantPhoto: ");
        mSucces = false;
        PlantPhotoDto plantPhotoDto = getPlantPhotoDto(plantPhoto);
        String token = "";
        Call<PlantPhotoDto> call = PlantApiClient.getInstance().getMyApi().updatePlantPhoto(token, plantPhotoDto, mAuth.getCurrentUser().getUid(), plantPhotoDto.getPhotoId());
        try {
            Response<PlantPhotoDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                mSucces = true;
                //uploadPhotos(plant.plantPhotos);
                //markPlantAsUploaded(response.body().getPlantId());
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
    }


    private PlantPhotoDto getPlantPhotoDto(PlantPhoto plantPhoto) {
        PlantPhotoDto plantPhotoDto = new PlantPhotoDto();

        plantPhotoDto.setPhotoId(plantPhoto.getPhotoId());
        plantPhotoDto.setCreatedBy(plantPhoto.getCreatedBy());
        plantPhotoDto.setPlantId(plantPhoto.getPlantId());
        plantPhotoDto.setMainPhoto(plantPhoto.isMainPhoto());
        plantPhotoDto.setPhotoName(plantPhoto.getPhotoName());
        plantPhotoDto.setUploadedPhotoReference(plantPhoto.getUploadedPhotoReference());
        plantPhotoDto.setDeleted(plantPhoto.isDeleted());
        return plantPhotoDto;
    }


    private void markPlantWithListsAsUploaded(PlantWithLists plantWithLists) {
        Log.d(TAG, "markPlantAsUploaded: ");
        mPlantRepository.markPlantAsUploaded(plantWithLists.plant.getPlantId());
        for (PlantPhoto plantPhoto : plantWithLists.plantPhotos) {
            if (plantPhoto.isSyncedWithCloud() == false) {
                markPlantPhotoAsUploaded(plantPhoto);
            }
        }
    }

    private void markPlantAsUploaded(Plant plant) {
        Log.d(TAG, "markPlantAsUploaded: " + plant.toString());
        mPlantRepository.markPlantAsUploaded(plant.getPlantId());
    }

    private void markPlantPhotoAsUploaded(PlantPhoto plantPhoto) {
        plantPhoto.setCreatedInCloud(true);
        plantPhoto.setSyncedWithCloud(true);
        updatePlantPhoto(plantPhoto);
    }


    private void uploadPhotos(List<PlantPhoto> plantPhotos) {
        Log.d(TAG, "uploadPhotos: #a");
        for (PlantPhoto plantPhoto : plantPhotos) {
            mSucces = false;
            if (plantPhoto.isPhotoUploaded() == false) {
                MediaItem mediaItem = uploadPhoto(plantPhoto);
                if (mSucces)
                    markPhotoAsUploaded(mediaItem, plantPhoto);
            }
        }
    }

    private MediaItem uploadPhoto(PlantPhoto plantPhoto) {
        Log.d(TAG, "uploadPhoto: " + plantPhoto.getPhotoName());
        uploadFile(plantPhoto);
        return null;
        //return uploadMedia(plantPhoto);

    }

    private void uploadFile(PlantPhoto plantPhoto) {
        mMediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        Uri file = Uri.fromFile(new File(mMediaStorageDir.getPath() + File.separator + plantPhoto.getPhotoName()));
        mImageFile = new File(mMediaStorageDir.getPath() + File.separator + plantPhoto.getPhotoName());

        StorageReference fileReference = mStorageRef.child(Utility.getCloudImageFolder() +"/" + plantPhoto.getPhotoName());

        fileReference.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: upload successfull");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: e.message" + e.getMessage());
                    }
                });
    }


    private void markPhotoAsUploaded(MediaItem mediaItem, PlantPhoto plantPhoto) {
        plantPhoto.setUploadedPhotoReference(mediaItem.getId());
        plantPhoto.setPhotoUploaded(true);
        updatePlantPhoto(plantPhoto);
    }



    private PlantWithLists getPlant(String id) {
        PlantWithLists plantWithLists = mPlantRepository.retrievePlantByIdSync(id);
        Log.d(TAG, "getPlant: title:" + plantWithLists.plant.getTitle());
        return plantWithLists;
    }

    private void sleep() {
        try {
            Thread.sleep(DELAY_TIME_MILLIS, 0);
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }



}
