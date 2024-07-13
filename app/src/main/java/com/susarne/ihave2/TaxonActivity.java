package com.susarne.ihave2;


import static com.susarne.ihave2.util.Collage.showCollage;
import static com.susarne.ihave2.util.Performance.speed;
import static com.susarne.ihave2.util.Utility.convertDpToPixel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.susarne.ihave2.models.IntentExtra.PhotoListActivityIntentExtra;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.Taxon;
import com.susarne.ihave2.persistence.PlantRepository;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TaxonActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "TaxonActivity";
    private static final String APP_TAG = "MyCustomApp";


    // ui component
    //nytfelt-detail

    private TableLayout mClassificationTableLayout;

    private TextView mDomainHeader, mDomain;
    private TextView mKingdom;
    private RelativeLayout mCheckContainer, mBackArrowContainer;

    private TextView mViewTitle;
    private TextInputLayout mPlantTitle, mPlantText;

    private ImageButton mCheck, mBackArrow;

    private ImageButton mEditButton;

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

        mPlantRepository = new PlantRepository(this);

        getIncomingIntent();

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
        setContentView(R.layout.activity_taxon);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditButton = findViewById(R.id.toolbar_edit);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditButton = findViewById(R.id.toolbar_edit);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mViewTitle = findViewById(R.id.plant_text_title);

        //nytfelt-detail

        mClassificationTableLayout = findViewById(R.id.taxon_table);

        mDomainHeader=findViewById(R.id.plant_domain_header);
        mDomain=findViewById(R.id.plant_domain);


        addRow("Række","Artimus");
        
    }

    private void addRow(String header, String taxonomy) {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(Color.BLACK);
        //create textview

        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, R.style.TableTextViewStyle);

        TextView tv1 = new TextView(themedContext,null,0);
        ViewGroup.LayoutParams tv1LayoutParams=mDomainHeader.getLayoutParams();
        tv1.setLayoutParams(tv1LayoutParams);
        tv1.setText(header);
        tr.addView(tv1);

        TextView tv2 = new TextView(themedContext,null,0);
        ViewGroup.LayoutParams tv2LayoutParams=mDomain.getLayoutParams();
        tv2.setLayoutParams(tv2LayoutParams);
        tv2.setText(taxonomy);
        tr.addView(tv2);


        mClassificationTableLayout.addView(tr);
    }


    private void setListeners() {

        mEditButton.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);


    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: 1");
        if (getIntent().hasExtra("selected_taxonId")) {
            Log.d(TAG, "getIncomingIntent: 2");
            String taxonId = getIntent().getStringExtra("selected_taxonId");
            Log.d(TAG, "getIncomingIntent: taxonid"+taxonId);
            getTaxon(taxonId);
            getTaxonhierarchy(taxonId);
        }
    }

    private void getTaxon(String taxonId) {
        mPlantRepository.getTaxonById(taxonId).observe(this, new Observer<Taxon>() {
            @Override
            public void onChanged(Taxon taxon) {
                setTaxonProperties(taxon);

            }
        });
    }

    private void getTaxonhierarchy(String taxonId) {
        mPlantRepository.getTaxonhierarchy(taxonId).observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> combNavne) {
                for (String combNavn:combNavne
                     ) {
                    Log.d(TAG, "onChanged: taxons-dansknavn"+combNavn);

                }

            }
        });
    }


    private void disableContentInteraction() {
        mCheckContainer.setVisibility(View.GONE);
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.VISIBLE);
    }


    private void setTaxonProperties(Taxon taxon) {
        Log.d(TAG, "setTaxonProperties: 1"+taxon.getDanskNavn());
        //nytfelt-detail
        mViewTitle.setText(taxon.getDanskNavn());
//        mPlantTitle.getEditText().setText(mInitialPlant.plant.getTitle());
//        mPlantText.getEditText().setText(mInitialPlant.plant.getContent());

    }






}