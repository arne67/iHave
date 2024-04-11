package com.example.ihave2;

import static com.example.ihave2.util.Collage.showCollage;
import static com.example.ihave2.util.Performance.speed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ihave2.api.GooglePhotoApiClient;
import com.example.ihave2.models.GooglePhotos.MediaItem;
import com.example.ihave2.models.IntentExtra.PhotoListActivityIntentExtra;
import com.example.ihave2.models.Plant;
import com.example.ihave2.models.PlantFlowerMonth;
import com.example.ihave2.models.PlantPhoto;
import com.example.ihave2.models.PlantWithLists;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.util.CurrentUser;
import com.example.ihave2.util.Utility;
import com.example.ihave2.viewmodels.PlantEditActivityViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantEditActivity extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "PlantEditActivity";
    private static final String APP_TAG = "MyCustomApp";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAIN_PHOTO_INTENT = 1;
    private static final int MULTI_PHOTO_INTENT = 2;
    private static final int CURRENT_USER_ID = 1;


    // ui component
    //nytfelt-detail
    private TextInputLayout mPlantTitle, mPlantText;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;
    private ImageView mMainPhoto;
    private TextView mMainPhotoButton;
    private ImageView[] mMultiPhoto = new ImageView[4];
    private GridLayout mCollage;
    private TextView mPhotocount;
    private TextView mMultiPhotoButton;
    private ImageButton mTestButton;
    private ImageButton mEditButton;
    private final CheckBox[] mCheckBoxFlower = new CheckBox[12];
    private AutoCompleteTextView mCategoryMenu;
    //private TextView mPlantId;

    // representing UI component
    private String mMainPhotoName;
    private Integer[] mCheckBoxFlowerIdxInEditedPlant = new Integer[12]; //indeholder checkboxens placering i mEditedPlant
    private PlantWithLists mEditedPlant;


    // vars
    private PlantRepository mPlantRepository;
    private ActivityResultLauncher<Intent> cameraResultLauncher, galleryResultLauncer;
    private File photoFile;
    private String mPhotoFileName;
    private int mCurrentPhotoIntent;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private File mMediaStorageDir, mImageFile;
    private long mTimeLast=System.currentTimeMillis();

    private String mAccessTokenString;
    private String mRefreshTokenString;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    PlantEditActivityViewModel mViewModel;

// start of public methods ********************************************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //private void xx1(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //createAlbum();
        speed(TAG,1);
        //restoreState();
        if (mAccessTokenString != null) {
            //getAlbums();
        }

        speed(TAG,2);
        setContentView(R.layout.activity_plant_edit);
        speed(TAG,3);
        mViewModel = new ViewModelProvider(this).get(PlantEditActivityViewModel.class);
        speed(TAG,4);
        mViewModel.initiate(getApplication());
        speed(TAG,5);
        connectToViewElements();
        speed(TAG,6);


        getCallbackFromCamera();
        speed(TAG,7);
        getCallbackFromGallery();
        speed(TAG,8);
        getCallbackFromPhotoListActivity();
        //getCallbackFromAuth();
        //getCallbackFromTokenRequest();

        speed(TAG,9);
        mPlantRepository = new PlantRepository(this);



        speed(TAG,10);
        mEditedPlant = mViewModel.getEditedPlant();
        speed(TAG,11);
        if (mEditedPlant != null) {
            speed(TAG,12);
            createRestOfEditedPlant();
            speed(TAG,13);
            copyEditedPlantToView();


        } else {
            if (getIncomingIntent()) {
                //this is an existing plant
                speed(TAG,14);
                createRestOfEditedPlant();
                speed(TAG,15);
                getSavedPlant();
                speed(TAG,16);

                //copyEditedPlantToView();
            } else {
                //this is a new plant
                speed(TAG,17);
                createRestOfEditedPlant();
                speed(TAG,18);
                setUserId();
                findPlantId();
                speed(TAG,19);
                setNewPlantView();
                speed(TAG,20);
                copyViewToEditedPlant();

            }
        }

        speed(TAG,21);
        setVisibility();
        speed(TAG,22);
        setListeners();
        speed(TAG,23);
        //helpers();


        //getMediaItem();

        //uploadMedia();
        //addToAlbum("CAISiQMAoe355Vc0Yu/DopIBXbBqi0fESgw/dy7rYeoct520qlzPq7U3TYMef50P5JhG17qT7BcMx/2HX48DkSH/clgCqMPl8W9tXHW4OdA9Nv1pXnOXflJHpcXQC7kemS6kHkLHZaYetWMRCQ0aKHN3fjxib1EYtd8ZRLsGcqtMjyJSBPtQ16fuy5PrUhvxxX8AbCWxGEjnZ65IVU8Z99/65Rq1Ii1gHs+xrTDjANRga4XecdXnD56QVd1Px6mHAS6yU0TXvA/X/zttwkJehXfA34Eo5E/rjM1/MSD4ONUzOkEusWU1OFDnsLwIEbFe6D9YuHbXjm0oKDYjV7exMHp5r2LWwPjRkihSIKHlaqPFPIbuIe3w77oGHxp7nFWtDWHgXfuvgc1ET7B6aaSRkCf3G+BlJBtKrezKUCgwpmcMMc6ou5Q9tPXekxiVn0aAncddTCLEPILyiegNcRCd3RzWvOdBDt6goayAyetA6/UdMhz9cDzzovztO54pvIZtxBsA5wO4VpAV7Qwb1rk");
        //downloadImage();
    }

    private void setUserId() {
        mEditedPlant.plant.setUserId(CurrentUser.getUserId());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: mPlantText: " + mPlantText.getEditText().getText().toString());
//        mViewModel.setPlantText(mPlantText.getEditText().getText().toString());
        copyViewToEditedPlant();
        storeEditedPlantInViewModel();
    }

    private void getSavedPlant() {
        Log.d(TAG, "getSavedPlant: ");
        mViewModel.setPlantId(mEditedPlant.plant.getPlantId());
        // Create the observer which updates the UI.
        final Observer<PlantWithLists> savedPlantObserver = new Observer<PlantWithLists>() {
            @Override
            public void onChanged(@Nullable final PlantWithLists plantWithLists) {
                // Update the UI,
                Log.d(TAG, "onChanged:plantwithlists"+plantWithLists);
                mViewModel.setSavedPlant(plantWithLists);
                Log.d(TAG, "onChanged: xxy "+plantWithLists.toString());
                mEditedPlant=new PlantWithLists(plantWithLists);
                Log.d(TAG, "onChanged: bb2-1 "+mEditedPlant.plantPhotos.toString());
                copyEditedPlantToView();

            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mViewModel.plant.observe(this, savedPlantObserver);
    }

    private void createRestOfEditedPlant() {
        if (mEditedPlant==null) mEditedPlant=new PlantWithLists();
        if (mEditedPlant.plant==null) mEditedPlant.plant = new Plant();
        if (mEditedPlant.plantFlowerMonths==null) mEditedPlant.plantFlowerMonths = new ArrayList<PlantFlowerMonth>();
        if (mEditedPlant.plantPhotos==null) mEditedPlant.plantPhotos = new ArrayList<PlantPhoto>();

    }

    private void storeEditedPlantInViewModel() {
        mViewModel.setEditedPlant(mEditedPlant);
    }

    private void copyViewToEditedPlant() {

        mEditedPlant.plant.setContent(mPlantText.getEditText().getText().toString());
        mEditedPlant.plant.setTitle(mPlantTitle.getEditText().getText().toString());
        String timestamp = Utility.getCurrentTimestamp();
        mEditedPlant.plant.setCreatedTime(timestamp);
        mEditedPlant.plant.setMainPhotoName(mMainPhotoName);
        Log.d(TAG, "storeEditedPlantFromViewModel: mMainPhotoName: " + mMainPhotoName);

        //mFinalPlant.plant.setCategory(); bliver sat når den vælges i menu
        Log.d(TAG, "storeEditedPlantInViewModel: mCategoriMenu: " + mCategoryMenu.toString());

        for (int i = 0; i < 12; i++) {
            if (mCheckBoxFlower[i].isChecked()) {
                Log.d(TAG, "copyViewToEditedPlant: "+i+" "+mCheckBoxFlower[i].isChecked()+" "+mCheckBoxFlowerIdxInEditedPlant[i] );
                if (mCheckBoxFlowerIdxInEditedPlant[i] == null) {
                    PlantFlowerMonth plantFlowerMonth = new PlantFlowerMonth();
                    plantFlowerMonth.setUserId(mEditedPlant.plant.getUserId());
                    plantFlowerMonth.setPlantId(mEditedPlant.plant.getPlantId());
                    plantFlowerMonth.setMonthNo(i + 1);
                    mEditedPlant.plantFlowerMonths.add(plantFlowerMonth);
                    mCheckBoxFlowerIdxInEditedPlant[i]=mEditedPlant.plantFlowerMonths.size()-1;
                } else {
                    if (mEditedPlant.plantFlowerMonths.get(mCheckBoxFlowerIdxInEditedPlant[i]).isDeleted()){
                        mEditedPlant.plantFlowerMonths.get(mCheckBoxFlowerIdxInEditedPlant[i]).setDeleted(false);
                    }
                }
            } else { // checkboxen er IKKE tjekket af
                if (!(mCheckBoxFlowerIdxInEditedPlant[i] == null) ){ // den er allerede oprettet i mEditedPlant
                    if (!mEditedPlant.plantFlowerMonths.get(mCheckBoxFlowerIdxInEditedPlant[i]).isDeleted()) { //den er ikke deleted i mEditedPlant
                        Log.d(TAG, "savePlant: bb1-1-1 "+mViewModel.getSavedPlant().toString());
                        mEditedPlant.plantFlowerMonths.get(mCheckBoxFlowerIdxInEditedPlant[i]).setDeleted(true);
                        Log.d(TAG, "savePlant: bb1-1-2 "+mViewModel.getSavedPlant().toString());
                    }
                }
            }
        }
        //mEditedPlant.plantPhotos bliver opdateret i forbindelse tilføjelse/sletning

        Log.d(TAG, "storeEditedPlantFromViewModel: MeditedPlant: " + mEditedPlant.toString());
    }

    private void copyEditedPlantToView() {
        //nytfelt-detail
        speed(TAG,101);
        mViewTitle.setText(mEditedPlant.plant.getTitle());
        mPlantTitle.getEditText().setText(mEditedPlant.plant.getTitle());
        //mPlantId.setText(String.valueOf(mEditedPlant.plant.getPlantId()));
        mPlantText.getEditText().setText(mEditedPlant.plant.getContent());

        mMainPhotoName = mEditedPlant.plant.getMainPhotoName();
        speed(TAG,102);
        showMainPhoto();
        speed(TAG,103);


        Log.d(TAG, "copyEditedPlantToView: mEditedPlant.plantFlowerMonths.size() "+mEditedPlant.plantFlowerMonths.size());
        Log.d(TAG, "copyEditedPlantToView: mEditedPlant.plantFlowerMonths.toString() bb2-1 "+mEditedPlant.plantFlowerMonths.toString());

        for (int i = 0; i < mEditedPlant.plantFlowerMonths.size(); i++) {
            int monthNo = mEditedPlant.plantFlowerMonths.get(i).getMonthNo();
            if (!mEditedPlant.plantFlowerMonths.get(i).isDeleted()){
                Log.d(TAG, "copyEditedPlantToView: bb2-1 "+i+" "+monthNo);
                mCheckBoxFlower[monthNo-1].setChecked(true);
            } else {
                mCheckBoxFlower[monthNo-1].setChecked(false);
            }
            mCheckBoxFlowerIdxInEditedPlant[monthNo-1]=i;
        }
        speed(TAG,104);


        showCollage(mEditedPlant.plantPhotos,mMultiPhoto,mPhotocount,this);

        speed(TAG,105);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        speed(TAG,106);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speed(TAG,107);
        mCategoryMenu.setAdapter(adapter);
        speed(TAG,108);
        mCategoryMenu.setText(getResources().getStringArray(R.array.category_array)[mEditedPlant.plant.getCategory()], false);
        speed(TAG,109);

    }


    private void showMainPhoto(){
        mMediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        Log.d(TAG, "showMainPhoto: "+mMainPhotoName);
        if (mMainPhotoName != null) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + mMainPhotoName);
            Bitmap bitmap = BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
            mMainPhoto.setBackgroundResource(android.R.color.transparent);
            Log.d(TAG, "showMainPhoto: bitmap: "+bitmap);
            mMainPhoto.setImageBitmap(bitmap);
            mMainPhotoButton.setText("Skift billede");
        } else {
            mMainPhoto.setBackgroundResource(R.drawable.ic_baseline_no_photography_24);
            mMainPhoto.setImageBitmap(null);
            mMainPhotoButton.setText("Tilføj billede");
        }
    }


    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: getid: "+view.getId());

        switch (view.getId()) {
            case R.id.toolbar_check: {
                speed(TAG,200);
                savePlant();
                speed(TAG,200);
                Log.d(TAG, "onClick: 1 xxy");
                speed(TAG,200);
                returnPlant();
                speed(TAG,200);
                Log.d(TAG, "onClick: 2");
                finish();
                break;
            }
            case R.id.plant_text_title: {
                //enableEditMode();
                break;
            }
            case R.id.toolbar_back_arrow: {

                break;
            }
            case R.id.main_photo_button: {
                Log.d(TAG, "onClick: main photo button");
                mCurrentPhotoIntent = MAIN_PHOTO_INTENT;
                showPhotoChoise(mMainPhotoButton);
                break;
            }
            case R.id.collage: {
                startPhotoList();
                Log.d(TAG, "onClick: collagexxx");
                // her kalder vi PhotoListActivity
                break;
            }
            case R.id.multi_photo_button: {
                Log.d(TAG, "onClick: multi photo button");
                //takeMultiPhoto();
                mCurrentPhotoIntent = MULTI_PHOTO_INTENT;
                showPhotoChoise(mMultiPhotoButton);
                break;
            }
            case R.id.test_button: {
                Log.d(TAG, "onClick: upload photo button");
                //uploadPhoto();
                //startOAuthAuthentication();
                break;
            }
            case R.id.toolbar_edit: {
                Log.d(TAG, "onClick: toolbar_edit");
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
            onClick(mCheck);
    }

// end of public methods  **********************************************************************************

// start of callback methods  **********************************************************************************


    private void getCallbackFromCamera() {
        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG, "onActivityResult: step 1");
                        Log.d(TAG, "onActivityResult: " + result.getResultCode());
                        if (result.getResultCode() == RESULT_OK) {
                            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
                            File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPhotoFileName);
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            savePhotoFromIntent(bitmap);


                        }

                    }
                }
        );

    }

    private void getCallbackFromGallery() {
        galleryResultLauncer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG, "onActivityResult: Gallery");
                        Log.d(TAG, "onActivityResult: " + result.getResultCode());
                        Log.d(TAG, "onActivityResult: result.getData: " + result);
                        Log.d(TAG, "onActivityResult: result.getData getData: " + result.getData().getData());
                        Uri inputUri = result.getData().getData();

                        try {
                            InputStream fileInputStream = PlantEditActivity.this.getContentResolver().openInputStream(inputUri);
                            mPhotoFileName = getPhotoFileName();
                            Log.d(TAG, "onActivityResult: mPhotoFIleName" + mPhotoFileName);
                            File targetFile = getPhotoFileUri(mPhotoFileName);
                            copyInputStreamToFile(fileInputStream, targetFile);


//                            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
//                            File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPhotoFileName);
                            Bitmap bitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
                            savePhotoFromIntent(bitmap);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
        );

    }

    private void savePhotoFromIntent(Bitmap bitmap) {
        switch (mCurrentPhotoIntent) {
            case MAIN_PHOTO_INTENT: {
                Log.d(TAG, "onActivityResult: mPhotoFileName");
                //saveSmallFile(bitmap, mPhotoFileName);
                addMultiPhoto(mPhotoFileName,true);
                showCollage(mEditedPlant.plantPhotos,mMultiPhoto,mPhotocount,this);
                mEditedPlant.plant.setMainPhotoName(mPhotoFileName);
                mMainPhotoName = mEditedPlant.plant.getMainPhotoName();
                showMainPhoto();
                break;
            }
            case MULTI_PHOTO_INTENT: {
                Log.d(TAG, "onActivityResult: MULTI_PHOTO_INTENT");
                addMultiPhoto(mPhotoFileName,false);
                showCollage(mEditedPlant.plantPhotos,mMultiPhoto,mPhotocount,this);
                break;
            }
        }
    }

    private void getCallbackFromPhotoListActivity() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            mEditedPlant = result.getData().getParcelableExtra("updated_plant");
//                            if (!mMainPhotoName.equals(mEditedPlant.plant.getMainPhotoName())){
                                mMainPhotoName=mEditedPlant.plant.getMainPhotoName();
                                showMainPhoto();
//                            }

//                            mInitialPlant = result.getData().getParcelableExtra("updated_plant");
//                            setPlantProperties();
                        }
                    }
                }
        );

    }


// end of callback methods  **********************************************************************************

    private void connectToViewElements() {

        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheck = findViewById(R.id.toolbar_check);
        mEditButton = findViewById(R.id.toolbar_edit);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);

        //nytfelt-detail
        mPlantText = findViewById(R.id.plant_text);
        //mPlantId = findViewById(R.id.plant_id);
        mViewTitle = findViewById(R.id.plant_text_title);
        mPlantTitle = findViewById(R.id.plant_title);
        mMainPhoto = findViewById(R.id.main_photo);
        mMainPhotoButton = findViewById(R.id.main_photo_button);
        mCheckBoxFlower[0] = findViewById(R.id.checkbox_flower_01);
        mCheckBoxFlower[1] = findViewById(R.id.checkbox_flower_02);
        mCheckBoxFlower[2] = findViewById(R.id.checkbox_flower_03);
        mCheckBoxFlower[3] = findViewById(R.id.checkbox_flower_04);
        mCheckBoxFlower[4] = findViewById(R.id.checkbox_flower_05);
        mCheckBoxFlower[5] = findViewById(R.id.checkbox_flower_06);
        mCheckBoxFlower[6] = findViewById(R.id.checkbox_flower_07);
        mCheckBoxFlower[7] = findViewById(R.id.checkbox_flower_08);
        mCheckBoxFlower[8] = findViewById(R.id.checkbox_flower_09);
        mCheckBoxFlower[9] = findViewById(R.id.checkbox_flower_10);
        mCheckBoxFlower[10] = findViewById(R.id.checkbox_flower_11);
        mCheckBoxFlower[11] = findViewById(R.id.checkbox_flower_12);
        mMultiPhotoButton = findViewById(R.id.multi_photo_button);
        mTestButton = findViewById(R.id.test_button);
        mCollage = findViewById(R.id.collage);
        mMultiPhoto[0] = findViewById(R.id.multi_photo1);
        mMultiPhoto[1] = findViewById(R.id.multi_photo2);
        mMultiPhoto[2] = findViewById(R.id.multi_photo3);
        mMultiPhoto[3] = findViewById(R.id.multi_photo4);
        mPhotocount = findViewById(R.id.photo_count);
        mCategoryMenu = findViewById(R.id.autoCompleteCategory);
        mCategoryMenu.setFreezesText(false);



    }

    private void setListeners() {
        //nytfelt-detail?
        mViewTitle.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mMainPhotoButton.setOnClickListener(this);
        mCollage.setOnClickListener(this);
        mMultiPhotoButton.setOnClickListener(this);
        mTestButton.setOnClickListener(this);
        mCategoryMenu.setOnItemClickListener(this);

    }

    private boolean getIncomingIntent() {

        if (getIntent().hasExtra("selected_plant")) {
            mEditedPlant = getIntent().getParcelableExtra("selected_plant");

            //nytfelt-detail
            mViewModel.setNewPlant(false);
            return true;
        }

        mViewModel.setNewPlant(true);
        return false;
    }




    private void setVisibility() {
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);
    }

    private void savePlant() {
        Log.d(TAG, "savePlant: gggg");
        //Log.d(TAG, "savePlant: bb1-1 "+mViewModel.getSavedPlant().toString());
        copyViewToEditedPlant();
        Log.d(TAG, "savePlant: bb2-1 "+mEditedPlant.plantFlowerMonths.toString());
        //Log.d(TAG, "savePlant: bb1-2 "+mViewModel.getSavedPlant().toString());
        storeEditedPlantInViewModel();
        //Log.d(TAG, "savePlant: bb1-9 "+mViewModel.getSavedPlant().toString());
        mViewModel.saveEditedPlant();
        //saveChanges();
    }


    private void saveSmallFile(Bitmap bitmap, String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        File smallImageFile = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        //Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 384, 512, false);
        try {/*from w  w  w.  j a v  a 2 s  .c  om*/
            FileOutputStream out = new FileOutputStream(smallImageFile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNewPlantView() {
        //nytfelt-detail - kun hvis default værdi

        //mPlantId.setText(String.valueOf(mEditedPlant.plant.getPlantId()));
        mViewTitle.setText("Plant title");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoryMenu.setAdapter(adapter);
        mCategoryMenu.setText(getResources().getStringArray(R.array.category_array)[0], false);

    }

    private void findPlantId() {
//        mPlantRepository.getMaxPlantId().observe(this, new Observer<Integer>() {
        mViewModel.getMaxPlantId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer plantId) {
                if (plantId != null) {
                    mEditedPlant.plant.setPlantId(plantId + 1);
                    //mPlantId.setText(String.valueOf(mEditedPlant.plant.getPlantId()));

                }

            }
        });
    }


    private void returnPlant() {
        // Put the String to pass back into an Intent and close this activity
        Intent intent = new Intent();
        intent.putExtra("updated_plant", mEditedPlant);
        setResult(RESULT_OK, intent);
    }


    private void showPhotoChoise(TextView textView) {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(PlantEditActivity.this, textView);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.add_photo, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Toast message on menu item clicked
                Toast.makeText(PlantEditActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                switch (menuItem.getItemId()){
                    case R.id.kamera:{
                        takePhoto();
                        break;
                    }
                    case R.id.galleri:{
                        selectFromGallery();
                        break;
                    }
                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.setForceShowIcon(true);
        popupMenu.show();
    }

    private void takeMainPhoto() {
        mCurrentPhotoIntent = MAIN_PHOTO_INTENT;
        takePhoto();
    }

    private void takeMultiPhoto() {
        mCurrentPhotoIntent = MULTI_PHOTO_INTENT;
        takePhoto();
    }


    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mPhotoFileName = getPhotoFileName();

        photoFile = getPhotoFileUri(mPhotoFileName);

        String fileProviderAuthority=getResources().getString(R.string.file_provider);

                Uri fileProvider = FileProvider.getUriForFile(PlantEditActivity.this, fileProviderAuthority, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, CAMERA_ACTION_CODE);
            cameraResultLauncher.launch(intent);
        } else {
            Toast.makeText(this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
        }

    }


    private void selectFromGallery() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
//        Intent intent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d(TAG, "selectFromGallery: 1");
        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, CAMERA_ACTION_CODE);
            Log.d(TAG, "selectFromGallery: 2");
            galleryResultLauncer.launch(intent);
        } else {
            Log.d(TAG, "selectFromGallery: 3");
            Toast.makeText(this, "There is no app that support this gallery action", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "selectFromGallery: 4");
    }

    // Returns the File for a photo stored on disk given the fileName
    private String getPhotoFileName() {

        String fileName;
        fileName = System.currentTimeMillis() + ".jpg";
        return fileName;
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        Log.d(TAG, "getPhotoFileUri: " + mediaStorageDir.getPath());
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    //todo-tjek om nedenstående 3 blot blot kan fjernes?
//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        Log.d(TAG, "onTextChanged: ");
//        mViewTitle.setText(charSequence.toString());
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//
//    }

    private void addMultiPhoto(String name, boolean mainPhoto) {
        PlantPhoto plantPhoto = new PlantPhoto();
        plantPhoto.setPlantId(mEditedPlant.plant.getPlantId());
        plantPhoto.setUserId(mEditedPlant.plant.getUserId());
        plantPhoto.setPhotoName(name);
        plantPhoto.setMainPhoto(mainPhoto);
        mEditedPlant.plantPhotos.add(plantPhoto);


    }


    private void startPhotoList() {
        Intent intent = new Intent(this, PhotoListActivity.class);
        PhotoListActivityIntentExtra photoListActivityIntentExtra=new PhotoListActivityIntentExtra();
        photoListActivityIntentExtra.setPlantWithLists(mEditedPlant);
        photoListActivityIntentExtra.setEditMode(true);
        intent.putExtra("selected_plant", photoListActivityIntentExtra);

        activityResultLauncher.launch(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: i" + i + " " + getResources().getStringArray(R.array.category_array)[i]);
        mEditedPlant.plant.setCategory(i);
    }





//    private void addToAlbum(String uploadToken) {
//        String bearerToken = "Bearer " + mAccessTokenString;
//        Log.d(TAG, "addToAlbum 1 ");
//        SimpleMediaItem simpleMediaItem = new SimpleMediaItem();
//        simpleMediaItem.setUploadToken(uploadToken);
//        NewMediaItem newMediaItem = new NewMediaItem();
//        newMediaItem.setDescription("dette er et ihave foto");
//        newMediaItem.setSimpleMediaItem(simpleMediaItem);
//        BatchCreateRequestBody batchCreateRequestBody = new BatchCreateRequestBody();
//        ArrayList<NewMediaItem> newMediaItems = new ArrayList<>();
//        newMediaItems.add(newMediaItem);
//        batchCreateRequestBody.setNewMediaItems(newMediaItems);
//        //mappe Ihave1
//        batchCreateRequestBody.setAlbumId("APmtqhqTsIwY1-adjsTFnWFS1S9yQynVN8fHuEi4cwYa___VZ0aEPZQA8vgNs2kBx7JlJQ7j7mpK");
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String jsonStr = gson.toJson(batchCreateRequestBody);
//        Log.d(TAG, "addToAlbum: jsonstr" + jsonStr);
//        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json"));
//        Call<String> call = GoogleApiClient.getInstance().getMyApi().addToAlbum(bearerToken, batchCreateRequestBody);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d(TAG, "onResponse: addToAlbum 1");
//                String myAlbums = response.body();
//                Log.d(TAG, "onResponse: addToAlbum " + response.code());
////                Log.d(TAG, "onResponse: createAlbum" + myAlbums.substring(0, 200));
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.d(TAG, "addToAlbum onFailure: 1" + t);
//                Log.d(TAG, "addToAlbum onFailure: 1" + call);
//                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
//            }
//
//        });
//
//    }


    private void getMediaItem() {
        String bearerToken = "Bearer " + mAccessTokenString;
        String mediaItemId = "APmtqhpX3H_hTbmP1FPNkXEONKRBE8pLCnXKCM5BjvFAvUSupr26yrKYdnLEB5x7Lgs7W9sQic9_g_w6WVqhoDceKq5dzULYow";
        Call<MediaItem> call = GooglePhotoApiClient.getInstance().getMyApi().getMediaItem(bearerToken, mediaItemId);
        call.enqueue(new Callback<MediaItem>() {
            @Override
            public void onResponse(Call<MediaItem> call, Response<MediaItem> response) {
                Log.d(TAG, "onResponse: getmediaitem ok svar");


                //String respbody = response.body();
                //Log.d(TAG, "onResponse: uploadMedia"+response.code());
                //Log.d(TAG, "onResponse: upload respbody " + respbody);
                Log.d(TAG, "onResponse: 1" + response.body().getDescription());
                //Log.d(TAG, "onResponse: 1"+response.body().getBaseUrl());
            }

            @Override
            public void onFailure(Call<MediaItem> call, Throwable t) {
                Log.d(TAG, "onFailure: getmediaitem 1");
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void downloadImage() {
        Runnable runnable = new Runnable() {
            public void run() {
                //some code here
                String imageURL = "https://lh3.googleusercontent.com/lr/AGiIYOXNckJeWODN7fkRrbd5CzS5TWGmALz4wAYgIK4itKpVmIu_rzeXWkk_ayynO4jtgi43VaRF1HFNfGnfs3bApCTkwkPz8smEhJQmKZWpHrsGn--0vrHnY2FM1RoXWM_2sMkI-8F-JuX3d2Yfl27oKSrbJwcANVMHO4XEmqXyvPfgTmll4nrL_W44eFCY1s8PcePy1SbnRmPSBlU5wBJ6M7cRVZae2eBZ3VV2WMSjivwsHUYqNHlYUc1JZfPVFadRy1xWMFc5CR-Azaw2ta8myHurcosqACumdtMaCUlPcyxCENsLOIqiUTUrV4dmbZ5fKqG5aSIzigB27wfcwZedYDSH32vsVsxX4e4rBzLD534AiHKKEtHDYQ-xmEfXDz5ZZwsvSBbovBNTXNRIBBcbwelEJKCXcOssGqwPiYHvpYMweC9FYoEjMJTn80hF2jfj0pNymITfYHl-hmf-ka0px9si9EiKQ2p0PjEfWqhCYTBkMhwVZ_yC3cLs3I3indkADp0fQ6rg4cIZacSGGVQUpO2fRXncWTNA_1cJkvVK7Q0k-dk3vGqh2rvla37qxSJGSuPCNjgu2ftm2UFy49NiLPL-Qmh_n8HNLzPnZtdqEiMOzWnm1XXRVrjwcn2sEtOhacWwpmnE5svM9s9pR3cwNZZFe-nBBcByBgKfSyiaUD4xuIG5c7dGS8Hc5zkPe8uYmKDMgmVUteH-PWwo2HqfKdQpt-xlaYI_u5gA238GwOJD8bRMJqMHX9dqAJherIWqTvr7O7etzxmmsjykhX0Y-hzD4OAulQyjUAJ7IAIm3msz0wxDrDQpsC65Fdk9v6OZIaIfWGgugFpBWkMOWmdqLbeJ05bESr6hzXFh80UU5xsqAFrFasgAJJdVD86uJnfAIuC8ahnIJMY3gohXpEMr38P3u-S9Pp6Q4dmaz7pFLjGlCiXJ3TsLID_d9KKWBaCvmA49uD0qtDvKZFYC1w";
                Bitmap bitmap;
                try {
                    // Download Image from URL
                    InputStream input = new java.net.URL(imageURL).openStream();
                    // Decode Bitmap
                    bitmap = BitmapFactory.decodeStream(input);
                    saveSmallFile(bitmap, getPhotoFileName());
                } catch (Exception e) {
                    Log.d(TAG, "run: fejl");
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void helpers() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mCheckBoxFlower[1].setChecked(true);
                Log.d(TAG, "onCreate: vi har sat februar til true");
            }
        }, 5000);   //5 seconds
        mCheckBoxFlower[1].setChecked(true);
    }

}
