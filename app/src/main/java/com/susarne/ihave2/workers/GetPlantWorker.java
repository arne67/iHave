package com.susarne.ihave2.workers;

import static com.susarne.ihave2.util.Constants.ACCESS_TOKEN_PHOTO;
import static com.susarne.ihave2.util.Constants.WORKER_PLANT_ID;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.susarne.ihave2.models.GooglePhotos.MediaItem;
import com.susarne.ihave2.models.Plant;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    public GetPlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: getplantworker1");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        int workerPlantId = getInputData().getInt(WORKER_PLANT_ID, 0);
        mContext = ContextSingleton.getContekst();
        mAccessTokenString = Token.getAccessToken(ACCESS_TOKEN_PHOTO);

        if (getFirstPlant() == null) {
            GooglePhoto.createPhotoDir(getApplicationContext());
            PlantsWithListsDto plantsWithListsDto = getPlantsForUserId(mAuth.getCurrentUser().getUid());
            Log.d(TAG, "doWork: blomstrer i md 11"+plantsWithListsDto.plants.get(1).isBloomsMonth11());
            Log.d(TAG, "doWork: plantsWithListsDto: " + plantsWithListsDto + "/" + plantsWithListsDto.toString());
            for (PlantWithListsDto plantWithListsDto : plantsWithListsDto.plants) {
                if (plantWithListsDto.getPlantPhotos()!=null){
                    for (PlantPhoto plantphoto : plantWithListsDto.getPlantPhotos()) {
                        Log.d(TAG, "doWork: " + plantphoto.getUploadedPhotoReference());
                        downloadImage(plantphoto);
//                        MediaItem mediaItem = GooglePhoto.getMediaItem(plantphoto.getUploadedPhotoReference());
//                        if (mediaItem != null) {
//                            GooglePhoto.downloadImage(mediaItem.getBaseUrl(), plantphoto.getPhotoName(), getApplicationContext());
//                        }
                    }
                }
                insertPlant(plantWithListsDto);


            }
            mPlantRepository.setLastGetUpdatedPlantsUntil(plantsWithListsDto.getUpdatedUntil());
        }
        return Result.success();
    }

    private void downloadImage(PlantPhoto plantPhoto) {
        Log.d(TAG, "downloadImage: plantphoto.getPhotoName(): "+plantPhoto.getPhotoName());

        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
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

    private static void saveFile(Bitmap bitmap, String photoFileName, Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        File smallImageFile = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        Log.d(TAG, "saveSmallFile: getpath: "+mediaStorageDir.getPath());
        Log.d(TAG, "saveSmallFile: getfilename: "+photoFileName);
        Log.d(TAG, "saveSmallFile: absolutPath: "+mediaStorageDir.getAbsolutePath());
        //Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 384, 512, false);
        try {/*from w  w  w.  j a v  a 2 s  .c  om*/
            smallImageFile.createNewFile();
            FileOutputStream out = new FileOutputStream(smallImageFile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getInputStreamFromUri(Context context, Uri uri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            return contentResolver.openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertPlant(PlantWithListsDto plantWithListsDto) {
        Plant plant = new Plant(plantWithListsDto);
        plant.setCreatedInCloud(true);
        plant.setSyncedWithCloud(true);
        mPlantRepository.insertPlantSync(plant);

        if (plantWithListsDto.getPlantPhotos()!=null){
            for (PlantPhoto p : plantWithListsDto.getPlantPhotos()) {
                PlantPhoto plantPhoto = new PlantPhoto(p);
                plantPhoto.setCreatedInCloud(true);
                plantPhoto.setSyncedWithCloud(true);
                mPlantRepository.insertPlantPhoto(plantPhoto);
            }
        }

    }


    private PlantsWithListsDto getPlantsForUserId(String userId) {
        //her skal vi kalde retrofit for at uploade synkront
        Call<PlantsWithListsDto> call = PlantApiClient.getInstance().getMyApi().getPlantsInUsersCommunities(userId);
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
