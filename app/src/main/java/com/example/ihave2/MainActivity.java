package com.example.ihave2;

import static com.example.ihave2.util.Constants.*;
import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.ihave2.adapters.PlantsRecyclerAdapter;
import com.example.ihave2.api.TestApiClient;
import com.example.ihave2.api.TokenApiClient;
import com.example.ihave2.models.PlantFlowerMonth;
import com.example.ihave2.models.PlantWithLists;
import com.example.ihave2.models.TokenRefreshDto;
import com.example.ihave2.models.TokenRefreshRequestDto;
import com.example.ihave2.models.TokenStrings;
import com.example.ihave2.models.test.PostItem;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.util.Constants;
import com.example.ihave2.util.Token;
import com.example.ihave2.workers.BackupWorker;
import com.example.ihave2.workers.RecoverWorker;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.chip.Chip;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        PlantsRecyclerAdapter.OnPlantListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";


    //UI components
    private RecyclerView mRecyclerView;
    private Chip mChipBlomst;
    private Toolbar mToolbar;

    //Vars
    private final ArrayList<PlantWithLists> mPlants = new ArrayList<>();
    private PlantsRecyclerAdapter mPlantRecyclerAdapter;
    private PlantRepository mPlantRepository;

    //Autorisation og tokens for Google Photo API
    private String mScopesPhoto = "https://www.googleapis.com/auth/photoslibrary.readonly https://www.googleapis.com/auth/photoslibrary.appendonly";
    private AuthorizationService mAuthorizationServicePhoto;
    private ActivityResultLauncher<Intent> mOAuthActivityResultLauncherPhoto;
    private AuthorizationService.TokenResponseCallback mTokenResponseCallbackPhoto;
    private AuthState mAuthStatePhoto;
    private TokenStrings mTokenStringsPhoto;

    //Autorisation og tokens for Google Drive API
    private String mScopesDrive = "https://www.googleapis.com/auth/drive.file";
    private AuthorizationService mAuthorizationServiceDrive;
    private ActivityResultLauncher<Intent> mOAuthActivityResultLauncherDrive;
    private AuthorizationService.TokenResponseCallback mTokenResponseCallbackDrive;
    private AuthState mAuthStateDrive;
    private TokenStrings mTokenStringsDrive;

    //--------------------------------------------



    private SharedPreferences mSharedPreferences;

    private String mMasterKeyAlias;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private WorkManager mWorkManager;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

// start of public methods ********************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: yy2");


        super.onCreate(savedInstanceState);
        //ContextSingleton.getInstance(getApplicationContext());
        //getSuperHeroes();
        connectToUi();
        getCallbackFromAuthPhoto();
        getCallbackFromTokenRequestPhoto();
        getCallbackFromAuthDrive();
        getCallbackFromTokenRequestDrive();
        mPlantRepository = new PlantRepository(this);
        //mPlantRepository.deleteAllPlants();
        setUiListeners();
        retrievePlants();
        initRecyclerView();
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("iHave");
        mToolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
//        setTitle("iHav2e");
        mChipBlomst.setChecked(false);


        //mAuthorizationServicePhoto = Token.startOAuthAuthentication(mScopesPhoto, mOAuthActivityResultLauncherPhoto, this);
        //starttestgoogleoauth();


        mTokenStringsPhoto = new TokenStrings();
        mTokenStringsPhoto.setAccessTokenString(Token.getAccessToken(ACCESS_TOKEN_PHOTO));
        //refreshtoken udløber efter 7 dage i teststatus. Herefter bliver vi nødt til
        //lade som om der ikke er nogen accesstoken så oauth-flowet startes
        //ved at fjerne udkommenteringen af linien herunder
        //mTokenStringsPhoto.setmAccessTokenStringPhoto(null);
        if (mTokenStringsPhoto.getAccessTokenString()==null){
            Log.d(TAG, "onCreate: startoa pga accesstoken=null");
            mAuthorizationServicePhoto = Token.startOAuthAuthentication(mScopesPhoto, mOAuthActivityResultLauncherPhoto, this);
        } else {
            getNewAccessTokenPhoto();
        }
/*
        mTokenStringsDrive = new TokenStrings();
        mTokenStringsDrive.setAccessTokenString(Token.getAccessToken(ACCESS_TOKEN_DRIVE));
        //refreshtoken udløber efter 7 dage i teststatus. Herefter bliver vi nødt til
        //lade som om der ikke er nogen accesstoken så oauth-flowet startes
        //ved at fjerne udkommenteringen af linien herunder
        //mTokenStringsDrive.setmAccessTokenString(null);
        if (mTokenStringsDrive.getAccessTokenString()==null){
            Log.d(TAG, "onCreate: startoa pga accesstoken=null");
            mAuthorizationServiceDrive = Token.startOAuthAuthentication(mScopesDrive, mOAuthActivityResultLauncherDrive, this);
        } else {
            getNewAccessTokenDrive();
        }
*/
        Log.d(TAG, "onCreate: yy2");

        Runnable runnable = new Runnable() {
            public void run() {

                //some code here
                //mPlantRepository.deleteAllPlants();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        Log.d(TAG, "onCreate: thread er startet");

    }

    private void getNewAccessTokenPhoto() {
        TokenRefreshRequestDto tokenRefreshRequestDto = new TokenRefreshRequestDto();

        tokenRefreshRequestDto.setClient_id(Token.getClientId(this));
        tokenRefreshRequestDto.setGrant_type("refresh_token");
        tokenRefreshRequestDto.setRefresh_token(Token.getRefreshToken(REFRESH_TOKEN_PHOTO));
        Call<TokenRefreshDto> call = TokenApiClient.getInstance().getMyApi().refreshToken(tokenRefreshRequestDto);
        call.enqueue(new Callback<TokenRefreshDto>() {
            @Override
            public void onResponse(Call<TokenRefreshDto> call, Response<TokenRefreshDto> response) {
                Log.d(TAG, "onResponse: getnewaccesstoken ok svar");
                //Log.d(TAG, "onResponse: 1" + response.body().getAccessToken());
                //Log.d(TAG, "onResponse: 1" + response.body().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: accesstoken: "+response.body().getAccess_token());
                    Token.putAccessToken(response.body().getAccess_token(),ACCESS_TOKEN_PHOTO);
                } else {
                    Log.d(TAG, "onResponse: startoa:"+response.code());
                    mAuthorizationServicePhoto =Token.startOAuthAuthentication(mScopesPhoto, mOAuthActivityResultLauncherPhoto, getApplicationContext());
                }
                //Log.d(TAG, "onResponse: 1"+response.body().getBaseUrl());
            }

            @Override
            public void onFailure(Call<TokenRefreshDto> call, Throwable t) {
                Log.d(TAG, "onFailure: getmediaitem 1");
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                //startOAuthAuthentication();
            }

        });
    }

    private void getNewAccessTokenDrive() {
        Log.d(TAG, "getNewAccessTokenDrive: ");
        TokenRefreshRequestDto tokenRefreshRequestDto = new TokenRefreshRequestDto();
        tokenRefreshRequestDto.setClient_id(Token.getClientId(this));
        tokenRefreshRequestDto.setGrant_type("refresh_token");
        tokenRefreshRequestDto.setRefresh_token(Token.getRefreshToken(REFRESH_TOKEN_DRIVE));
        Call<TokenRefreshDto> call = TokenApiClient.getInstance().getMyApi().refreshToken(tokenRefreshRequestDto);
        call.enqueue(new Callback<TokenRefreshDto>() {
            @Override
            public void onResponse(Call<TokenRefreshDto> call, Response<TokenRefreshDto> response) {
                Log.d(TAG, "onResponse: getnewaccesstoken ok svar drive");
                //Log.d(TAG, "onResponse: 1" + response.body().getAccessToken());
                //Log.d(TAG, "onResponse: 1" + response.body().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: accesstoken: drive "+response.body().getAccess_token());
                    Token.putAccessToken(response.body().getAccess_token(),ACCESS_TOKEN_DRIVE);
                    Runnable runnable = new Runnable() {
                        public void run() {

                                Log.d(TAG, "run: før forsøg på opret folder i drive");
                                //GoogleDrive.createDriveFile(mTokenStringsDrive.getmAccessTokenString(),"atest223",getApplicationContext());
                                Log.d(TAG, "run: efter forsøg på opret folder i drive");

                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                } else {
                    Log.d(TAG, "onResponse: startoa: drive "+response.code());
                    mAuthorizationServiceDrive =Token.startOAuthAuthentication(mScopesDrive, mOAuthActivityResultLauncherDrive, getApplicationContext());
                }
                //Log.d(TAG, "onResponse: 1"+response.body().getBaseUrl());
            }

            @Override
            public void onFailure(Call<TokenRefreshDto> call, Throwable t) {
                Log.d(TAG, "onFailure: getmediaitem 1 drive ");
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                //startOAuthAuthentication();
            }

        });
    }


    private void getTestPost(int key) {
        Log.d(TAG, "getTestPost: 1");
        String keyString=Integer.toString(key);
        Log.d(TAG, "getTestPost: keyString"+keyString);
        Call<PostItem> call = TestApiClient.getInstance().getMyApi().getPost(keyString);
        call.enqueue(new Callback<PostItem>() {
            @Override
            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                Log.d(TAG, "onResponse: 1");
                Log.d(TAG, "onResponse: response.body().title: "+response.body().id+" "+response.body().title);
            }

            @Override
            public void onFailure(Call<PostItem> call, Throwable t) {
                Log.d(TAG, "onFailure getPost: 1");
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                Log.d(TAG, "getTestPost onFailure: 1" + t);


            }

        });
    }

    @Override
    //når der vælges en plante i oversigten
    public void onPlantClick(int position) {
        Log.d(TAG, "onPlantClick: " + position);
        Intent intent = new Intent(this, PlantActivity.class);
        intent.putExtra("selected_plant", mPlants.get(position));
        startActivity(intent);
    }

    @Override
    //når der klikkes på et de UI-elementer der har en OnclickListener
    public void onClick(View view) {

        switch (view.getId()) {
            // +-ikonet (opret plante)

            case R.id.fab: {
                Intent intent = new Intent(this, PlantEditActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.chip_blomst: {
                Log.d(TAG, "onClick: chip_1" + mChipBlomst.isChecked());
                if (mChipBlomst.isChecked()) {
                    //filterPlantsWithLen3();
                    filterPlantsFlowerNow();
                } else {
                    retrievePlants();
                }
                break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.plant_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.clearFocus();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: " + s);
                String likestring = "%" + s + "%";
                Log.d(TAG, "onQueryTextChange: " + likestring);
                retrievePlantsWithTitle(likestring);
                return false;
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.reset_login:{
                        Log.d(TAG, "onMenuItemClick: reset login");
                        reset_login();
                        break;
                    }
                    case R.id.backup_copy:{
                        Log.d(TAG, "onMenuItemClick: backup copy");
                        backup_copy();
                        break;
                    }
                    case R.id.recover_copy:{
                        Log.d(TAG, "onMenuItemClick: recover copy");
                        recover_copy();
                        break;
                    }
                    case R.id.action_search:{
                        Log.d(TAG, "onMenuItemClick: searchbar");
                        break;
                    }
                }

                return false;
            }
        });



        return true;
    }

    private void backup_copy() {
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest savePlantRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .build();
        mWorkManager.enqueue(savePlantRequest);

    }
    private void recover_copy() {
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest savePlantRequest =
                new OneTimeWorkRequest.Builder(RecoverWorker.class)
                        .build();
        mWorkManager.enqueue(savePlantRequest);

    }


    // end of public methods  *********************************************************************************************************

    // start of callback methods  **********************************************************************************
    private void getCallbackFromAuthPhoto() {
        // Vi er kommet retur fra consent screen præsenteret for brugeren
        mOAuthActivityResultLauncherPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "startOAuthAuthentication: xxxx");
                Log.d(TAG, "onActivityResult: resultcode: "+result.getResultCode());

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
                    AuthorizationException ex = AuthorizationException.fromIntent(data);
                    Log.d(TAG, "onActivityResult: AuthorizationResponse" + resp);
                    Log.d(TAG, "onActivityResult: AuthorizationResponse authorizationCode" + resp.authorizationCode);

                    if (resp != null && resp.authorizationCode != null) {
                        mAuthStatePhoto = new AuthState(resp, ex);

                        mAuthorizationServicePhoto.performTokenRequest(resp.createTokenExchangeRequest(), mTokenResponseCallbackPhoto);
                        // app received the authorizationCode
                        // resp.authorizationCode

                    }
                }
            }
        });
    }

    private void getCallbackFromTokenRequestPhoto() {
        mTokenResponseCallbackPhoto = new AuthorizationService.TokenResponseCallback() {
            //Vi har fået vekslet vores authorizationCode til en accesstoken
            @Override
            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException authException) {
                mTokenStringsPhoto=Token.receivedTokenResponse(tokenResponse, authException, mAuthStatePhoto);
                Token.putAccessToken(mTokenStringsPhoto.getAccessTokenString(),ACCESS_TOKEN_PHOTO);
                Token.putRefreshToken(mTokenStringsPhoto.getRefreshTokenString(),REFRESH_TOKEN_PHOTO);

                //showPhotolist();
            }
        };
    }

    private void getCallbackFromAuthDrive() {
        // Vi er kommet retur fra consent screen præsenteret for brugeren
        mOAuthActivityResultLauncherDrive = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "onActivityResult: getCallbackFromAuthDrive");
                Log.d(TAG, "startOAuthAuthentication: xxxx");
                Log.d(TAG, "onActivityResult: resultcode: "+result.getResultCode());

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
                    AuthorizationException ex = AuthorizationException.fromIntent(data);
                    Log.d(TAG, "onActivityResult: AuthorizationResponse" + resp);
                    Log.d(TAG, "onActivityResult: AuthorizationResponse authorizationCode" + resp.authorizationCode);

                    if (resp != null && resp.authorizationCode != null) {
                        mAuthStateDrive = new AuthState(resp, ex);

                        mAuthorizationServiceDrive.performTokenRequest(resp.createTokenExchangeRequest(), mTokenResponseCallbackDrive);
                        // app received the authorizationCode
                        // resp.authorizationCode

                    }
                }
            }
        });
    }

    private void getCallbackFromTokenRequestDrive() {
        mTokenResponseCallbackDrive = new AuthorizationService.TokenResponseCallback() {
            //Vi har fået vekslet vores authorizationCode til en accesstoken
            @Override
            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException authException) {
                Log.d(TAG, "onTokenRequestCompleted: getCallbackFromTokenRequestDrive");
                mTokenStringsDrive=Token.receivedTokenResponse(tokenResponse, authException, mAuthStateDrive);
                Token.putAccessToken(mTokenStringsDrive.getAccessTokenString(),ACCESS_TOKEN_DRIVE);
                Token.putRefreshToken(mTokenStringsDrive.getRefreshTokenString(),REFRESH_TOKEN_DRIVE);
                //showPhotolist();
            }
        };
    }


    // end of callback methods  **********************************************************************************

    private void connectToUi() {
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mChipBlomst = findViewById(R.id.chip_blomst);
        mToolbar = findViewById(R.id.plants_toolbar);


    }

    private void setUiListeners() {
        mChipBlomst.setOnClickListener(this);
        findViewById(R.id.fab).setOnClickListener(this);
    }


    private void retrievePlants() {
        mPlantRepository.retrievePlantsTask().observe(this, new Observer<List<PlantWithLists>>() {
            @Override
            public void onChanged(List<PlantWithLists> plants) {
                if (mPlants.size() > 0) {
                    mPlants.clear();
                }
                if (plants != null) {
                    mPlants.addAll(plants);
                }
                for (PlantWithLists i : mPlants) {
                    Log.d(TAG, "onChanged: arnx " + i.plant.getPlantId());

                }
                mPlantRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void retrievePlantsWithTitle(String streng) {
        mPlantRepository.retrievePlantsTask2(streng).observe(this, new Observer<List<PlantWithLists>>() {
            @Override
            public void onChanged(List<PlantWithLists> plants) {
                if (mPlants.size() > 0) {
                    mPlants.clear();
                }
                if (plants != null) {
                    mPlants.addAll(plants);
                }

                mPlantRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterPlantsWithLen3() {
        mPlants.removeIf(t -> t.plant.getTitle().length() != 3);
        mPlantRecyclerAdapter.notifyDataSetChanged();
    }
    private void filterPlantsFlowerNow() {
        LocalDate currentdate = LocalDate.now();
        int month= currentdate.getMonthValue();
        Log.d(TAG, "filterPlantsFlowerNow: month"+month);
        mPlants.removeIf(t -> !findPlantFlowerMonthByMonthNo(t.plantFlowerMonths,month));
        mPlantRecyclerAdapter.notifyDataSetChanged();
    }

    private boolean findPlantFlowerMonthByMonthNo(List<PlantFlowerMonth> plantFlowerMonths, int monthNo) {
        Optional<PlantFlowerMonth> plantFlowerMonth;
        plantFlowerMonth = plantFlowerMonths.stream()
                .filter(pfm -> pfm.getMonthNo() == monthNo)
                .findFirst();
        if (plantFlowerMonth.isPresent()) return true;
        else return false;
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),linearLayoutManager.getOrientation());
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xfff7f7f7, 0xfff7f7f7});
        drawable.setColor(Color.BLACK);
        drawable.setSize(1,1);
        dividerItemDecoration.setDrawable(drawable);

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mPlantRecyclerAdapter = new PlantsRecyclerAdapter(mPlants, this);
        mRecyclerView.setAdapter(mPlantRecyclerAdapter);
    }

    private void deletePlant(PlantWithLists plant) {
        mPlants.remove(plant);
        mPlantRecyclerAdapter.notifyDataSetChanged();
        //mPlantRepository.deletePlant(plant.plant);
        plant.plant.setDeleted(true);
        mPlantRepository.updatePlant(plant.plant);
    }

    private final ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deletePlant(mPlants.get(viewHolder.getAdapterPosition()));

        }
    };

    private void reset_login() {
        mSharedPreferences=initiateSharedPreferences();
        mSharedPreferences.edit().remove(Constants.IHAVE_USER_NAME).apply();
        mSharedPreferences.edit().remove(Constants.IHAVE_PASSWORD).apply();
    }


}
