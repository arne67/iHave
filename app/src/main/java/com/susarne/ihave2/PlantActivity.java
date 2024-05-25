package com.susarne.ihave2;


import static com.susarne.ihave2.util.Collage.showCollage;
import static com.susarne.ihave2.util.Performance.speed;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.susarne.ihave2.models.IntentExtra.PhotoListActivityIntentExtra;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.persistence.PlantRepository;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.Collections;

public class PlantActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PlantActivity";
    private static final String APP_TAG = "MyCustomApp";


    // ui component
    //nytfelt-detail

    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private TextInputLayout mPlantTitle, mPlantText;

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


    // vars

    private PlantWithLists mInitialPlant;

    private int mMode;
    private PlantRepository mPlantRepository;
    private File mMediaStorageDir, mImageFile;

    private ActivityResultLauncher<Intent> activityResultLauncher;

// start of public methods ********************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 1");
        super.onCreate(savedInstanceState);

        connectToUi();

        getCallbackFromPlantEditActivity();
        mPlantRepository = new PlantRepository(this);

        getIncomingIntent();
        setPlantProperties();
        disableContentInteraction();
        setListeners();
        Log.d(TAG, "onCreate: 999");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }

            case R.id.toolbar_edit: {
                Log.d(TAG, "onClick: toolbar_edit");
                editPlant();
                break;
            }
            case R.id.collage: {
                startPhotoList();
                Log.d(TAG, "onClick: collagexxx");
                // her kalder vi PhotoListActivity
                break;
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


// end of public methods  **********************************************************************************

// start of callback methods  **********************************************************************************


    private void getCallbackFromPlantEditActivity() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        speed(TAG, 301);
                        Log.d(TAG, "onActivityResult: arny 1 ");
                        Log.d(TAG, "onActivityResult: arny 2 " + result.getResultCode());
                        if (result.getResultCode() == RESULT_OK) {
                            Log.d(TAG, "onActivityResult: arny 3");
                            speed(TAG, 302);
                            mInitialPlant = result.getData().getParcelableExtra("updated_plant");
                            speed(TAG, 303);
                            setPlantProperties();
                            speed(TAG, 304);

                        }

                    }
                }
        );

    }

// end of callback methods  **********************************************************************************

    private void connectToUi() {
        setContentView(R.layout.activity_plant);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditButton = findViewById(R.id.toolbar_edit);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
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

        mEditButton.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mCollage.setOnClickListener(this);

    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: 1");
        if (getIntent().hasExtra("selected_plant")) {
            Log.d(TAG, "getIncomingIntent: 2");
            mInitialPlant = getIntent().getParcelableExtra("selected_plant");
        }
    }


    private void disableContentInteraction() {
        mCheckContainer.setVisibility(View.GONE);
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.VISIBLE);
    }


    private void setPlantProperties() {
        Log.d(TAG, "setPlantProperties: 1");
        //nytfelt-detail
        mViewTitle.setText(mInitialPlant.plant.getTitle());
        mPlantTitle.getEditText().setText(mInitialPlant.plant.getTitle());
        mPlantText.getEditText().setText(mInitialPlant.plant.getContent());
        mMainPhotoName = mInitialPlant.plant.getMainPhotoName();
        Collections.sort(mInitialPlant.plantPhotos, (c1, c2) -> c1.getPhotoName().compareTo(c2.getPhotoName()));
        showCollage(mInitialPlant.plantPhotos, mMultiPhoto, mPhotocount, this);

        speed(TAG, 102);
        //showMainPhoto();
        speed(TAG, 103);


        if (mInitialPlant.plant.isBloomsMonth1()) mCheckBoxFlower[0].setChecked(true);
        else mCheckBoxFlower[0].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth2()) mCheckBoxFlower[1].setChecked(true);
        else mCheckBoxFlower[1].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth3()) mCheckBoxFlower[2].setChecked(true);
        else mCheckBoxFlower[2].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth4()) mCheckBoxFlower[3].setChecked(true);
        else mCheckBoxFlower[3].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth5()) mCheckBoxFlower[4].setChecked(true);
        else mCheckBoxFlower[4].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth6()) mCheckBoxFlower[5].setChecked(true);
        else mCheckBoxFlower[5].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth7()) mCheckBoxFlower[6].setChecked(true);
        else mCheckBoxFlower[6].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth8()) mCheckBoxFlower[7].setChecked(true);
        else mCheckBoxFlower[7].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth9()) mCheckBoxFlower[8].setChecked(true);
        else mCheckBoxFlower[8].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth10()) mCheckBoxFlower[9].setChecked(true);
        else mCheckBoxFlower[9].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth11()) mCheckBoxFlower[10].setChecked(true);
        else mCheckBoxFlower[10].setChecked(false);
        if (mInitialPlant.plant.isBloomsMonth12()) mCheckBoxFlower[11].setChecked(true);
        else mCheckBoxFlower[11].setChecked(false);
        speed(TAG, 104);

        //showCollage();


        speed(TAG, 105);

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //             R.array.category_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mCategoryMenu.setAdapter(adapter);
        mCategoryMenu.setText(getResources().getStringArray(R.array.category_array)[mInitialPlant.plant.getCategory()], false);

        showMainPhoto();
    }

    private void showMainPhoto(){
        mMediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (mMainPhotoName != null) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + mInitialPlant.plant.getMainPhotoName());
            Bitmap bitmap = BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
            mMainPhoto.setBackgroundResource(android.R.color.transparent);
            mMainPhoto.setImageBitmap(bitmap);
        } else {
            mMainPhoto.setBackgroundResource(R.drawable.ic_baseline_no_photography_24);
            mMainPhoto.setImageBitmap(null);
        }
    }

//    private void editPlant() {
//        Log.d(TAG, "editPlant: ");
//        Intent intent = new Intent(this,PlantEditActivity.class);
//        intent.putExtra("selected_plant",mInitialPlant);
//        startActivity(intent);
//        Log.d(TAG, "editPlant: afteractivity");
//    }

    private void editPlant() {
        Log.d(TAG, "editPlant: ");
        Intent intent = new Intent(this, PlantEditActivity.class);
        intent.putExtra("selected_plant", mInitialPlant);
        Log.d(TAG, "editPlant: mMainPhotoName: " + mMainPhotoName);
        activityResultLauncher.launch(intent);
    }

    private void startPhotoList() {
        Intent intent = new Intent(this, PhotoListActivity.class);
        PhotoListActivityIntentExtra photoListActivityIntentExtra=new PhotoListActivityIntentExtra();
        photoListActivityIntentExtra.setPlantWithLists(mInitialPlant);
        photoListActivityIntentExtra.setEditMode(false);
        intent.putExtra("selected_plant", photoListActivityIntentExtra);
        activityResultLauncher.launch(intent);
    }

}