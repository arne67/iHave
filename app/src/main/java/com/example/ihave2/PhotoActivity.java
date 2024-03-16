package com.example.ihave2;


import static com.example.ihave2.util.Constants.ACTION_DELETE_PHOTO;
import static com.example.ihave2.util.Constants.ACTION_NONE;
import static com.example.ihave2.util.Constants.ACTION_SELECT_MAIN_PHOTO;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toolbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.exifinterface.media.ExifInterface;

import com.example.ihave2.models.IntentExtra.PhotoActivityIntentExtra;
import com.example.ihave2.persistence.PlantRepository;

import java.io.File;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PlantActivity";
    private static final String APP_TAG = "MyCustomApp";


    // ui component
    View mParentView;
    // toolbarfelter
    Toolbar mToolbar;
    private TextView mViewTitle;
    private ImageButton mEditButton;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;
    //nytfelt-detail


    private ImageView mMainPhoto;

    //private TextView mPlantId;

    // representing UI component
    private String mMainPhotoName;


    // vars

    private PhotoActivityIntentExtra mPhotoActivityIntentExtra;

    private int mMode;
    private String mAction=ACTION_NONE;
    private PlantRepository mPlantRepository;

    private ActivityResultLauncher<Intent> activityResultLauncher;

// start of public methods ********************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 1");
        super.onCreate(savedInstanceState);

        connectToUi();
        setSupportActionBar(mToolbar);

        mPlantRepository = new PlantRepository(this);

        getIncomingIntent();
        setPlantProperties();
        setContentInteraction();
        setListeners();
        Log.d(TAG, "onCreate: 999");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.toolbar_back_arrow:
            case R.id.toolbar_check: {
                returnAction();
                finish();
                break;
            }

            case R.id.toolbar_edit: {
                Log.d(TAG, "onClick: toolbar_edit");
                //editPlant();
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




// end of callback methods  **********************************************************************************

    private void connectToUi() {
        setContentView(R.layout.activity_photo);
        mParentView = findViewById(R.id.parent_view);
        mToolbar = findViewById(R.id.plants_toolbar);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditButton = findViewById(R.id.toolbar_edit);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheck = findViewById(R.id.toolbar_check);

        //nytfelt-detail

        //mPlantId = findViewById(R.id.plant_id);
        mViewTitle = findViewById(R.id.plant_text_title);

        mMainPhoto = findViewById(R.id.main_photo);



    }


    private void setListeners() {

        mEditButton.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mCheck.setOnClickListener(this);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.foto_info:{
                        Log.d(TAG, "onMenuItemClick: fotoinfo");
                        showPhotoInfo();
                        break;
                    }
                    case R.id.select_main_photo:{
                        mAction=ACTION_SELECT_MAIN_PHOTO;
                        break;
                    }
                    case R.id.delete_photo:{
                        mAction=ACTION_DELETE_PHOTO;
                        returnAction();
                        finish();
                        break;
                    }
                }

                return false;
            }
        });

    }

    private void showPhotoInfo() {
        // inflate the layout of the popup window
        String metaData = hentMetaData();
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.foto_info_popup, null);
        TextView metaDataText=popupView.findViewById(R.id.meta_data_text);
        Log.d(TAG, "showPhotoInfo: metadata: "+metaData+"/"+metaDataText);
        metaDataText.setText(metaData);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private String hentMetaData() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        String filePath = mediaStorageDir.getPath() + File.separator + mPhotoActivityIntentExtra.getPhotoName();
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String metaString="Oprettet:\n"+exifInterface.getAttribute(ExifInterface.TAG_DATETIME);

        return metaString;
        /*
        return exifInterface.getAttribute(ExifInterface.TAG_DATETIME+'/'
                +ExifInterface.TAG_SUBJECT_LOCATION+'/'
                +ExifInterface.TAG_GPS_LONGITUDE+'/'
                +ExifInterface.TAG_SUBJECT_LOCATION+'/'
                );

         */

    }


    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: 1");
        if (getIntent().hasExtra("selected_photo")) {
            Log.d(TAG, "getIncomingIntent: 2");
            //plantPhotoActivityIntentExtra = new PlantPhotoActivityIntentExtra();
            mPhotoActivityIntentExtra = getIntent().getParcelableExtra("selected_photo");
            Log.d(TAG, "getIncomingIntent: PhotoName: "+ mPhotoActivityIntentExtra.getPhotoName());
        }
    }


    private void setContentInteraction() {
        if (mPhotoActivityIntentExtra.isEditMode()){
            mBackArrowContainer.setVisibility(View.GONE);
            mCheckContainer.setVisibility(View.VISIBLE);
        }
        else {
            mBackArrowContainer.setVisibility(View.VISIBLE);
            mCheckContainer.setVisibility(View.GONE);
        }
        mEditButton.setVisibility(View.GONE);
    }


    private void setPlantProperties() {
        Log.d(TAG, "setPlantProperties: 1cc");
        //nytfelt-detail
        mViewTitle.setText(mPhotoActivityIntentExtra.getPlantTitle());

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPhotoActivityIntentExtra.getPhotoName());
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        mMainPhoto.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        if (!mPhotoActivityIntentExtra.isEditMode()){
            MenuItem item;
            item=menu.findItem(R.id.delete_photo);
            item.setVisible(false);
            item=menu.findItem(R.id.select_main_photo);
            item.setVisible(false);
        }
        return true;
    }
    private void returnAction() {
        // Put the String to pass back into an Intent and close this activity
        Intent intent = new Intent();
        intent.putExtra("action", mAction);
        setResult(RESULT_OK, intent);
    }


}