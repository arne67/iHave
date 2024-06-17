package com.susarne.ihave2;

import static com.susarne.ihave2.util.Constants.*;

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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.api.services.drive.model.User;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.susarne.ihave2.adapters.PlantsRecyclerAdapter;
import com.susarne.ihave2.api.TestApiClient;
import com.susarne.ihave2.api.TokenApiClient;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.models.TokenRefreshDto;
import com.susarne.ihave2.models.TokenRefreshRequestDto;
import com.susarne.ihave2.models.TokenStrings;
import com.susarne.ihave2.models.test.PostItem;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.services.FirebaseMessageService;
import com.susarne.ihave2.util.Token;
import com.susarne.ihave2.util.CurrentUser;
import com.susarne.ihave2.workers.BackupWorker;
import com.susarne.ihave2.workers.GetPlantWorker;
import com.susarne.ihave2.workers.GetUpdatedPlantWorker;
import com.susarne.ihave2.workers.RecoverWorker;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.chip.Chip;
import com.susarne.ihave2.workers.SavePlantWorker;
import com.susarne.ihave2.workers.SystemStartWorker;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        PlantsRecyclerAdapter.OnPlantListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String APP_TAG = "MyCustomApp";


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
    private static final String PERIODIC_GET_WORK_TAG = "PERIODIC_GET_WORK_TAG";

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;



// start of public methods ********************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "..onCreate: yy2");


        super.onCreate(savedInstanceState);
        //ContextSingleton.getInstance(getApplicationContext());
        //getSuperHeroes();
        mAuth = FirebaseAuth.getInstance();
        startSystemStartWorker();


        //getNewUpdatedPlantsPeriodically();
        connectToUi();
//        getCallbackFromAuthPhoto();
//        getCallbackFromTokenRequestPhoto();
//        getCallbackFromAuthDrive();
//        getCallbackFromTokenRequestDrive();
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

/*
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



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "..onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "..onStop: ");
        WorkManager.getInstance(this).cancelAllWorkByTag(PERIODIC_GET_WORK_TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "..onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "..onPause: ");
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
        intent.putExtra("selected_plantId", mPlants.get(position).plant.getPlantId());
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
                if (s.equals("sannehack")){
                    Log.d(TAG, "onQueryTextChange: sannehack");
                    enableTestMenu(menu);
                }
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
                    case R.id.get_new_updated_plants:{
                        Log.d(TAG, "onMenuItemClick: get new updated plants");
                        getNewUpdatedPlants();
                        break;
                    }
                    case R.id.delete_all_plants:{
                        Log.d(TAG, "onMenuItemClick: delete alle plants");
                        deleteAllPlant();
                        break;
                    }
                    case R.id.get_user_plants:{
                        Log.d(TAG, "onMenuItemClick: get user plants");
                        startGetPlantWorker();
                        break;
                    }
                    case R.id.create_firebase_user:{
                        Log.d(TAG, "onMenuItemClick: create_firebase_user");
                        createFirebaseUser();
                        break;
                    }
                    case R.id.create_firebase_token:{
                        Log.d(TAG, "onMenuItemClick: create_firebase_user");
                        createFirebaseToken();
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

    private void enableTestMenu(Menu menu) {
        menu.findItem(R.id.reset_login).setVisible(true);
        menu.findItem(R.id.backup_copy).setVisible(true);
        menu.findItem(R.id.recover_copy).setVisible(true);
        menu.findItem(R.id.get_new_updated_plants).setVisible(true);
        menu.findItem(R.id.delete_all_plants).setVisible(true);
        menu.findItem(R.id.get_user_plants).setVisible(true);
        menu.findItem(R.id.create_firebase_token).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
   }

    private void createFirebaseUser() {
        mAuth.createUserWithEmailAndPassword("kofoedspost@hotmail.com", "mak987x")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: createfirebaseuser ok");
                            sendConfirmEmail(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createFirebaseToken() {
        FirebaseMessageService messagingService = new FirebaseMessageService();
        messagingService.fetchTokenManually();
    }
    private void sendConfirmEmail(FirebaseUser user) {
        user.sendEmailVerification();
    }

    private void deleteAllPlant() {
        Runnable runnable = new Runnable() {
            public void run() {
                mPlantRepository.deleteAllPlants();
                deleteAllImages();
                mPlantRepository.deleteSystem();

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void deleteAllImages() {
        File directory = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (directory.exists() && directory.isDirectory()) {
            // Få alle filer i mappen
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // Slet filen
                        Log.d(TAG, "deleteAllImages: file.getName: "+file.getName());
                        boolean deleted=true;
                        deleted = file.delete();
                        if (!deleted) {
                            // Håndter hvis filen ikke kunne slettes
                            System.out.println("Kunne ikke slette filen: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

    }

    private void getNewUpdatedPlants() {
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest getPlantRequest =
                new OneTimeWorkRequest.Builder(GetUpdatedPlantWorker.class)
                        .build();
        mWorkManager.enqueue(getPlantRequest);

    }

    private void getNewUpdatedPlantsPeriodically() {
        Log.d(TAG, "..on getNewUpdatedPlantsPeriodically: ");
        mWorkManager = WorkManager.getInstance(getApplication());
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(GetUpdatedPlantWorker.class, 30, TimeUnit.MINUTES)
                        .addTag(PERIODIC_GET_WORK_TAG)
                        .build();
        mWorkManager.enqueue(periodicWorkRequest);

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
                startGetPlantWorker();


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
                Log.d(TAG, "onChanged: plants.size()"+plants.size());
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
        Iterator<PlantWithLists> iterator = mPlants.iterator();
        while (iterator.hasNext()) {
            PlantWithLists currentPlant = iterator.next();
            if (!currentPlant.plant.bloomsInCurrentMonth()) {
                iterator.remove(); // Fjern elementet, hvis det opfylder kriteriet
            }
        }
        mPlantRecyclerAdapter.notifyDataSetChanged();
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
        plant.plant.setSyncedWithCloud(false);

        mPlantRepository.update(plant.plant).observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                startSavePlantWorker();
            }
        });

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
         //mSharedPreferences.edit().remove(Constants.IHAVE_USER_NAME).apply();
        CurrentUser.deleteAccessToken();
        mAuth.signOut();
    }

    private void startGetPlantWorker() {
        WorkManager mWorkManager;
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest getPlantsRequest =
                new OneTimeWorkRequest.Builder(GetPlantWorker.class)
                        .build();
        mWorkManager.enqueue(getPlantsRequest);

    }

    private void startSystemStartWorker() {
        WorkManager mWorkManager;
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest getPlantsRequest =
                new OneTimeWorkRequest.Builder(SystemStartWorker.class)
                        .build();
        mWorkManager.enqueue(getPlantsRequest);

    }

    private void startSavePlantWorker() {
        // Add WorkRequest to Cleanup temporary images
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest savePlantRequest =
                new OneTimeWorkRequest.Builder(SavePlantWorker.class)
                        .build();
        mWorkManager.enqueue(savePlantRequest);
    }

}
