package com.example.ihave2.ui.login;

import static com.example.ihave2.util.Constants.WORKER_PLANT_ID;
import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihave2.BuildConfig;
import com.example.ihave2.MainActivity;
import com.example.ihave2.R;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.databinding.ActivityLoginBinding;
import com.example.ihave2.util.Constants;
import com.example.ihave2.util.ContextSingleton;
import com.example.ihave2.util.CurrentUser;
import com.example.ihave2.workers.BackupWorker;
import com.example.ihave2.workers.GetPlantWorker;
import com.example.ihave2.workers.SavePlantWorker;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private String mUserName;
    private String mPassword;
    private String mAccessToken;
    private static String mMasterKeyAlias;

    // UI - components
    EditText mUsernameEditText;
    EditText mPasswordEditText;
    EditText mFullNameEditText;
    EditText mEmailEditText;
    Button mLoginButton;
    Button mRegisterButton;
    Button mSelectLoginButton;
    Button mSelectRegisterButton;
    ProgressBar mLoadingProgressBar;


    private static final String TAG = "LoginActivity";

    PlantRepository mPlantRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSingleton.getInstance(getApplicationContext());

        mPlantRepository = new PlantRepository(this);

        initiateUi();
        setListeners();
        startLoginObservers();



        Log.d(TAG, "onCreate: buildType: " + BuildConfig.BUILD_TYPE.toString());
        //Log.d(TAG, "onCreate: apibase: "+getResources().getString(R.string.apibase));

//        deleteLogin();
//        CurrentUser.deletePhotoAlbumId();
//        CurrentUser.deletePhotoAlbumIdUploaded();



        if (getSavedLogin()) {
            startMainActivity();
            finish();
        } else {
            if (unconfirmedAccountCreation()) {
                setStateUnconfirmed();
            } else {
                setStateRegisterOrLogin();
            }
        }

        //startLoginProcedure();
    }

    private void startGetPlantWorker() {
        WorkManager mWorkManager;
        mWorkManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest getPlantsRequest =
                new OneTimeWorkRequest.Builder(GetPlantWorker.class)
                        .build();
        mWorkManager.enqueue(getPlantsRequest);

    }

    private void setListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loginbutton");
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString());
            }
        });
        mSelectLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selectloginbutton");
                setStateLogin();
            }
        });
        mSelectRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selectregisterbutton");
                startRegisterActivity();
            }
        });

    }

    private boolean isRegisterValid() {
        return true;
    }

    private void register() {
    }

    private void initiateUi() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        mUsernameEditText = binding.username;
        mPasswordEditText = binding.password;
        mFullNameEditText = binding.fullName;
        mEmailEditText = binding.email;
        mLoginButton = binding.login;
        mRegisterButton = binding.register;
        mSelectRegisterButton = binding.selectRegister;
        mSelectLoginButton = binding.selectLogin;
        mLoadingProgressBar = binding.loading;
    }


    private void setStateUnconfirmed() {
        binding.unconfirmedText.setVisibility(View.VISIBLE);

    }


    private void setStateRegisterOrLogin() {
        binding.username.setVisibility(View.GONE);
        binding.password.setVisibility(View.GONE);
        binding.fullName.setVisibility(View.GONE);
        binding.email.setVisibility(View.GONE);
        binding.login.setVisibility(View.GONE);
        binding.register.setVisibility(View.GONE);
        binding.selectLogin.setVisibility(View.VISIBLE);
        binding.selectRegister.setVisibility(View.VISIBLE);
        binding.unconfirmedText.setVisibility(View.GONE);

        mSelectRegisterButton.setEnabled(true);
        mSelectLoginButton.setEnabled(true);

    }


    private void setStateLogin() {
        binding.username.setVisibility(View.VISIBLE);
        binding.password.setVisibility(View.VISIBLE);
        binding.fullName.setVisibility(View.INVISIBLE);
        binding.email.setVisibility(View.INVISIBLE);
        binding.login.setVisibility(View.VISIBLE);
        binding.register.setVisibility(View.GONE);
        binding.selectLogin.setVisibility(View.GONE);
        binding.selectRegister.setVisibility(View.GONE);
        binding.unconfirmedText.setVisibility(View.GONE);

        mLoginButton.setEnabled(true);

    }



    private boolean unconfirmedAccountCreation() {
        return false;
    }

    private void startLoginObservers() {
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                Log.d(TAG, "onChanged: getloginformstate ");
                if (loginFormState == null) {
                    return;
                }
                mLoginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    Log.d(TAG, "onChanged: usernameerror");
                    mUsernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    mPasswordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                Log.d(TAG, "onChanged: getLoginResult ");
                if (loginResult == null) {
                    return;
                }
                mLoadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    if (loginResult.isOnlyMissingConfirmation()){
                        setStateUnconfirmed();
                    } else {
                        showLoginFailed(loginResult.getError());
                    }
                }
                if (loginResult.getSuccess() != null) {

                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    saveLogin(loginResult.getSuccess());
                    startMainActivity();
                    startGetPlantWorker();
                    finish();
                }

                //Complete and destroy login activity once successful

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserName = mUsernameEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                loginViewModel.loginDataChanged(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString());
                if (mUsernameEditText.getText().toString().equals("d")) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            //some code here
                            Log.d(TAG, "run: visletter");
                            //mPlantRepository.deleteAllPlants();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        };
        mUsernameEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(mUsernameEditText.getText().toString(),
                            mPasswordEditText.getText().toString());
                }
                return false;
            }
        });

    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void startRegisterActivity() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }


    private boolean getSavedLogin() {
        mAccessToken= CurrentUser.getAccessToken();
        if (mAccessToken==null) return false;
        else return true;

    }

    private void saveLogin(LoggedInUserView loggedInUserView) {
        CurrentUser.putUserId(loggedInUserView.getUserId());
        CurrentUser.putAccessToken(loggedInUserView.getAccessToken());
    }

    private void deleteLogin() {
        CurrentUser.deleteUserId();
        CurrentUser.deleteAccessToken();
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();

        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}