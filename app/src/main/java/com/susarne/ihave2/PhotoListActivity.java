package com.susarne.ihave2;

import static com.susarne.ihave2.util.Performance.speed;
import static com.susarne.ihave2.util.Constants.ACTION_DELETE_PHOTO;
import static com.susarne.ihave2.util.Constants.ACTION_SELECT_MAIN_PHOTO;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.susarne.ihave2.adapters.PhotosRecyclerAdapter;
import com.susarne.ihave2.models.IntentExtra.PhotoActivityIntentExtra;
import com.susarne.ihave2.models.IntentExtra.PhotoListActivityIntentExtra;
import com.susarne.ihave2.models.PhotoListElement;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.util.VerticalSpacingItemDecorator;

import java.util.ArrayList;

public class PhotoListActivity extends AppCompatActivity implements
        PhotosRecyclerAdapter.OnPlantListener,
        View.OnClickListener {

    private static final String TAG = "PhotoListactivity";

    //UI components
    private TextView mViewTitle;
    private ImageButton mEditButton;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private RecyclerView mRecyclerView;


    //Vars
    //input ti photolistactivity
    PhotoListActivityIntentExtra mPhotoListActivityIntentExtra;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private int mCurrentPosition;

    //liste hvor idx peger på index i tilsvarende liste i mPhotoListActivityIntentExtra (input til photolistactivity)
    private final ArrayList<PhotoListElement> mPhotos = new ArrayList<>();
    // ui component
    //nytfelt-detail
    private PhotosRecyclerAdapter mPhotosRecyclerAdapter;

    private ImageButton mCheck, mBackArrow;

// start of public methods ********************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        connectToUi();
        setUiListeners();
        //retrievePhotosForPlant(0);
        getIncomingIntent();
        setPlantProperties();
        setContentInteraction();
        initRecyclerView();
        getCallbackFromPhotoactivity();

        setSupportActionBar(findViewById(R.id.plants_toolbar));
        //setTitle(mPhotoListActivityIntentExtra.plant.getTitle());

    }

    @Override
    //når der vælges et foto i oversigten
    public void onPlantClick(int position) {
        Log.d(TAG, "onPlantClick: "+position);
        int idx = mPhotos.get(position).getIdx();
        PhotoActivityIntentExtra extra = new PhotoActivityIntentExtra();
        extra.setPhotoName(mPhotoListActivityIntentExtra.getPlantPhotos().get(idx).getPhotoName());
        extra.setPlantTitle(mPhotoListActivityIntentExtra.getTitle());
        extra.setEditMode(mPhotoListActivityIntentExtra.isEditMode());
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("selected_photo", extra);
        Log.d(TAG, "onPlantClick: xy: "+mPhotoListActivityIntentExtra.getPlantPhotos().get(position).getPhotoName());
        mCurrentPosition=position;
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onMainPhotoButtonClicked(int position) {
        Log.d(TAG, "onMainPhotoButtonClicked: ");
        mPhotoListActivityIntentExtra.setMainPhotoName(mPhotos.get(position).getPlantPhoto().getPhotoName());
        Log.d(TAG, "onMainPhotoButtonClicked: mainphotoname: "+mPhotoListActivityIntentExtra.getMainPhotoName());
    }

    @Override
    //når der klikkes på et de UI-elementer der har en OnclickListener
    public void onClick(View view) {
        Log.d(TAG, "onClick: xxa");
        switch (view.getId()) {
            case R.id.toolbar_check: {
                speed(TAG,101);
                returnPlantPhotos();
                speed(TAG,102);
                Log.d(TAG, "onClick: check ");
                speed(TAG,103);
                finish();
                break;
            }
            case R.id.toolbar_back_arrow: {
                Log.d(TAG, "onClick: back ");
                finish();
                break;
            }


        }


    }



// end of public methods  *********************************************************************************************************

// start of callback methods  **********************************************************************************

    private void getCallbackFromPhotoactivity() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            String action = result.getData().getStringExtra("action");
                            switch (action) {
                                case ACTION_DELETE_PHOTO: {
                                    int idx = mPhotos.get(mCurrentPosition).getIdx();
                                    mPhotoListActivityIntentExtra.getPlantPhotos().get(idx).setDeleted(true);
                                    Log.d(TAG, "onActivityResult: setnulltest "+mPhotoListActivityIntentExtra.getPlantPhotos().get(idx).getPhotoName()+"/"+mPhotoListActivityIntentExtra.getMainPhotoName());
                                    if (mPhotoListActivityIntentExtra.getPlantPhotos().get(idx).getPhotoName().equals(mPhotoListActivityIntentExtra.getMainPhotoName())){
                                        Log.d(TAG, "onActivityResult: setnullexec");
                                        mPhotoListActivityIntentExtra.setMainPhotoName(null);
                                    }
                                    mPhotos.remove(mCurrentPosition);
                                    mPhotosRecyclerAdapter.notifyDataSetChanged();
                                    break;
                                }
                                case ACTION_SELECT_MAIN_PHOTO: {
                                    Log.d(TAG, "onActivityResult: fromphotoactivity mCurrentPosition: "+mCurrentPosition+"/"+mPhotos.get(mCurrentPosition).getPlantPhoto().getPhotoName());
                                    mPhotoListActivityIntentExtra.setMainPhotoName(mPhotos.get(mCurrentPosition).getPlantPhoto().getPhotoName());
                                    break;
                                }

                            }

                        }
                    }
                }
        );

    }


// end of callback methods  **********************************************************************************

    private void connectToUi() {
        setContentView(R.layout.activity_photo_list);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditButton = findViewById(R.id.toolbar_edit);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mViewTitle = findViewById(R.id.plant_text_title);

        mRecyclerView = findViewById(R.id.recyclerView);
    }

    private void setUiListeners() {
        mCheck.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
    }

    private void initRecyclerView(){
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mPhotosRecyclerAdapter = new PhotosRecyclerAdapter(mPhotos,this);
        mRecyclerView.setAdapter(mPhotosRecyclerAdapter);
    }


    private final ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //TODO - slet photo i array list
            //mPhotos.get(viewHolder.getAdapterPosition()).setDeleted(true);
            int idx = mPhotos.get(viewHolder.getAdapterPosition()).getIdx();
            mPhotoListActivityIntentExtra.getPlantPhotos().get(idx).setDeleted(true);
            mPhotos.remove(viewHolder.getAdapterPosition());
            mPhotosRecyclerAdapter.notifyDataSetChanged();
        }
    };

    private void getIncomingIntent() {
        if (getIntent().hasExtra("photoListActivityIntentExtra")) {

            mPhotoListActivityIntentExtra=getIntent().getParcelableExtra("photoListActivityIntentExtra");
             int idx=0;
            for (PlantPhoto p:mPhotoListActivityIntentExtra.getPlantPhotos()) {
                if (!p.isDeleted()){
                    PhotoListElement photoListElement = new PhotoListElement(idx,p);
                    mPhotos.add(photoListElement);
                }
                idx++;
            }
        }
    }
    private void setPlantProperties() {
        Log.d(TAG, "setPlantProperties: 1");
        //nytfelt-detail
        mViewTitle.setText(mPhotoListActivityIntentExtra.getTitle());
    }
    private void setContentInteraction() {
        if (mPhotoListActivityIntentExtra.isEditMode()){
            mBackArrowContainer.setVisibility(View.GONE);
            mCheckContainer.setVisibility(View.VISIBLE);
        }
        else {
            mBackArrowContainer.setVisibility(View.VISIBLE);
            mCheckContainer.setVisibility(View.GONE);
        }
        mEditButton.setVisibility(View.GONE);
    }
    private void returnPlantPhotos() {
        // Put the String to pass back into an Intent and close this activity
        Intent intent = new Intent();
        Log.d(TAG, "returnPlantPhotos: mPhotoListActivityIntentExtra: "+mPhotoListActivityIntentExtra.toString());
        intent.putExtra("photoListActivityIntentExtra", mPhotoListActivityIntentExtra);
        setResult(RESULT_OK, intent);
    }

}