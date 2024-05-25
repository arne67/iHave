package com.susarne.ihave2.workers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.susarne.ihave2.util.Constants.*;

import com.google.firebase.auth.FirebaseAuth;
import com.susarne.ihave2.api.GooglePhotoApiClient;
import com.susarne.ihave2.api.GooglePhotoApiScalarsClient;
import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.models.GetUserRespondDto;
import com.susarne.ihave2.models.GooglePhotos.Album;
import com.susarne.ihave2.models.GooglePhotos.AlbumsCreateRequestBody;
import com.susarne.ihave2.models.GooglePhotos.BatchCreateRequestBody;
import com.susarne.ihave2.models.GooglePhotos.BatchCreateRespondBody;
import com.susarne.ihave2.models.GooglePhotos.MediaItem;
import com.susarne.ihave2.models.GooglePhotos.NewMediaItem;
import com.susarne.ihave2.models.GooglePhotos.NewMediaItemResult;
import com.susarne.ihave2.models.GooglePhotos.SimpleMediaItem;
import com.susarne.ihave2.models.PhotoAlbumIdDto;
import com.susarne.ihave2.models.Plant;
import com.susarne.ihave2.models.PlantDto;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantPhotoDto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.util.ContextSingleton;
import com.susarne.ihave2.util.Token;
import com.susarne.ihave2.util.CurrentUser;
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


public class SavePlantWorker extends Worker {
    private static final String TAG = "SavePlantWorker";
    private static final String APP_TAG = "MyCustomApp";
    private PlantRepository mPlantRepository;
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;
    private File mMediaStorageDir, mImageFile;

    private FirebaseAuth mAuth;

    public SavePlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        mAuth = FirebaseAuth.getInstance();

        int workerPlantId = getInputData().getInt(WORKER_PLANT_ID, 0);
        sleep();
        mContext = ContextSingleton.getContekst();
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

        if (CurrentUser.getPhotoAlbumId()==null && !CurrentUser.isPhotoAlbumIdUploaded()){
            uploadAlbumId(CurrentUser.getPhotoAlbumId());
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
        token="xxx";
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
        for (PlantPhoto p:plant.plantPhotos) {
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
        Call<PlantDto> call = PlantApiClient.getInstance().getMyApi().updatePlant(token, plantDto, plantDto.getCreatedBy(), plantDto.getPlantId());
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
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
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
        Call<PlantPhotoDto> call = PlantApiClient.getInstance().getMyApi().updatePlantPhoto(token, plantPhotoDto,plantPhotoDto.getCreatedBy(),plantPhotoDto.getPhotoId());
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
        return uploadMedia(plantPhoto);

    }

    private void markPhotoAsUploaded(MediaItem mediaItem, PlantPhoto plantPhoto) {
        plantPhoto.setUploadedPhotoReference(mediaItem.getId());
        plantPhoto.setPhotoUploaded(true);
        updatePlantPhoto(plantPhoto);
    }


    private void getPlant(PlantWithLists plant) {
        //her skal vi kalde retrofit for at uploade synkront
        Call<PlantWithListsDto> call = PlantApiClient.getInstance().getMyApi().getPlant("1", "4");
        try {
            Response<PlantWithListsDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                Log.d(TAG, "uploadPlant: " + response.body().getTitle());
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }

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

    private MediaItem uploadMedia(PlantPhoto plantPhoto) {
        String bearerToken = "Bearer " + mAccessTokenString;
        Log.d(TAG, "uploadMedia: 1");
        mMediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        mImageFile = new File(mMediaStorageDir.getPath() + File.separator + plantPhoto.getPhotoName());
        RequestBody requestBody = RequestBody.create(mImageFile, MediaType.parse("application/octet"));
        Call<String> call = GooglePhotoApiScalarsClient.getInstance().getMyApi().uploadMedia(bearerToken, requestBody);
        try {
            Response<String> response = call.execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "onResponse: uploadMedia 2");
                String respbody = response.body();
                Log.d(TAG, "onResponse: uploadMedia" + response.code());
                Log.d(TAG, "onResponse: upload respbody " + respbody);
                String uploadToken = respbody;
                return addToAlbum(uploadToken);
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadMedia: failed" + response.errorBody().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadMedia: io-fejl");
            return null;
        }

    }


    private MediaItem addToAlbum(String uploadToken) {
        String bearerToken = "Bearer " + mAccessTokenString;
        Log.d(TAG, "addToAlbum 1 ");
        SimpleMediaItem simpleMediaItem = new SimpleMediaItem();
        simpleMediaItem.setUploadToken(uploadToken);
        NewMediaItem newMediaItem = new NewMediaItem();
        newMediaItem.setDescription("dette er et ihave foto");
        newMediaItem.setSimpleMediaItem(simpleMediaItem);
        BatchCreateRequestBody batchCreateRequestBody = new BatchCreateRequestBody();
        ArrayList<NewMediaItem> newMediaItems = new ArrayList<>();
        newMediaItems.add(newMediaItem);
        batchCreateRequestBody.setNewMediaItems(newMediaItems);
        //mappe Ihave1
        batchCreateRequestBody.setAlbumId("APmtqhqTsIwY1-adjsTFnWFS1S9yQynVN8fHuEi4cwYa___VZ0aEPZQA8vgNs2kBx7JlJQ7j7mpK");
        batchCreateRequestBody.setAlbumId(getAlbumId());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(batchCreateRequestBody);
        Log.d(TAG, "addToAlbum: jsonstr" + jsonStr);
        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json"));
        Call<BatchCreateRespondBody> call = GooglePhotoApiClient.getInstance().getMyApi().addToAlbum(bearerToken, batchCreateRequestBody);
        try {
            Response<BatchCreateRespondBody> response = call.execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "onResponse: addToAlbum 1");
                mSucces = true;
                List<NewMediaItemResult> newMediaItemResults = response.body().getNewMediaItemResults();
                Log.d(TAG, "onResponse: addToAlbum newMediaItemResults: " + newMediaItemResults);
                return newMediaItemResults.get(0).mediaItem;

                //String mediaItemId=newMediaItemResults.get(0).mediaItem.getId();
                //return mediaItemId;
            } else {
                // handle unsuccessful response
                Log.d(TAG, "addToAlbum: failed" + response.errorBody().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "addToAlbum: io-fejl");
            return null;
        }

    }

    private String getAlbumId() {
        String photoAlbumId = CurrentUser.getPhotoAlbumId();
        Log.d(TAG, "getAlbumId: photoAlbumId 1: "+photoAlbumId);
        if (photoAlbumId==null){
            photoAlbumId=getPhotoAlbumIdFromCloud();
            Log.d(TAG, "getAlbumId: photoAlbumId 2: "+photoAlbumId);
            if (photoAlbumId==null){
                return createPhotoAlbum();
            } else {
                CurrentUser.putPhotoAlbumId(photoAlbumId);
                return photoAlbumId;
            }
        } else {
            CurrentUser.putPhotoAlbumId(photoAlbumId);
            return photoAlbumId;
        }
        
    }

    private String getPhotoAlbumIdFromCloud() {
        //her skal vi kalde retrofit for at uploade synkront
        Log.d(TAG, "uploadPlantPhoto: ");
        mSucces = false;
        String token = "";
        Call<GetUserRespondDto> call = PlantApiClient.getInstance().getMyApi().getUser(mAuth.getCurrentUser().getUid());
        try {
            Response<GetUserRespondDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                mSucces = true;
                return response.body().getPhotoAlbumId();
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
            return null;
        }


    }

    private String createPhotoAlbum() {
        String albumId;
        String bearerToken = "Bearer " + mAccessTokenString;
        Log.d(TAG, "createAlbum 1 ");
        Album album = new Album();
        album.setTitle("iHave1");
        AlbumsCreateRequestBody albumsCreateRequestBody = new AlbumsCreateRequestBody();
        albumsCreateRequestBody.setAlbum(album);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(albumsCreateRequestBody);
        Log.d(TAG, "createAlbum: jsonstr" + jsonStr);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Call<Album> call = GooglePhotoApiClient.getInstance().getMyApi().createAlbum(bearerToken, requestBody);
        try {
            Response<Album> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                mSucces = true;
                Log.d(TAG, "uploadPlant: " + response.body());
                String photoAlbumId=response.body().getId();
                CurrentUser.putPhotoAlbumId(photoAlbumId);
                uploadAlbumId(photoAlbumId);
                return photoAlbumId;
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
            return null;
        }
    }

    private void uploadAlbumId(String photoAlbumId) {
        //her skal vi kalde retrofit for at uploade synkront
        Log.d(TAG, "uploadAlbumId: ");
        mSucces = false;
        PhotoAlbumIdDto photoAlbumIdDto = new PhotoAlbumIdDto();
        photoAlbumIdDto.setPhotoAlbumId(photoAlbumId);

        String token = "";
        Call<Void> call = PlantApiClient.getInstance().getMyApi().updatePhotoAlbumId(token, mAuth.getCurrentUser().getUid(),photoAlbumIdDto);
        try {
            Response<Void> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: " + response.body());
                mSucces = true;
                CurrentUser.putPhotoAlbumIdUploaded(true);
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
}
