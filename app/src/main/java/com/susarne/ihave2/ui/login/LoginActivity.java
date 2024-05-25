package com.susarne.ihave2.ui.login;

import static com.google.common.base.Predicates.isNull;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.susarne.ihave2.BuildConfig;
import com.susarne.ihave2.MainActivity;
import com.susarne.ihave2.R;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.databinding.ActivityLoginBinding;
import com.susarne.ihave2.util.ContextSingleton;
import com.susarne.ihave2.util.CurrentUser;

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

    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    PlantRepository mPlantRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSingleton.getInstance(getApplicationContext());

        mPlantRepository = new PlantRepository(this);

        mAuth = FirebaseAuth.getInstance();

        initiateUi();
        setListeners();
        startLoginObservers();


        Log.d(TAG, "onCreate: buildType: " + BuildConfig.BUILD_TYPE.toString());
        //Log.d(TAG, "onCreate: apibase: "+getResources().getString(R.string.apibase));

//        deleteLogin();
//        CurrentUser.deletePhotoAlbumId();
//        CurrentUser.deletePhotoAlbumIdUploaded();


        Log.d(TAG, "onCreate: getsavedlogin2");

//        if (getSavedLogin()) {
//            Log.d(TAG, "onCreate: startmain");
//            startMainActivity();
//            finish();
//        } else {
//            if (unconfirmedAccountCreation()) {
//                setStateUnconfirmed();
//            } else {
//                setStateRegisterOrLogin();
//            }
//        }
        //firebase authentification i stedet for
        //mAuth.signOut(); //vi signer ud for at teste at det er en ny bruger
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "onCreate: startmain");
            startMainActivity();
            finish();
        } else {
            setStateRegisterOrLogin();
        }

    }


    private void setListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loginbutton");
                mLoadingProgressBar.setVisibility(View.VISIBLE);

                login(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString());
//                loginViewModel.login(mUsernameEditText.getText().toString(),
//                        mPasswordEditText.getText().toString());
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

    private void login(String email, String password) {
        Log.d(TAG, "login: "+email+"/"+password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                // Proceed with authenticated and verified user
                                setResult(Activity.RESULT_OK);
                                //savelogin (userid og accesstoken) skal ske ved svar på registrering i railway
                                //saveLogin(loginResult.getSuccess());
                                initiateAtLogin();
                                startMainActivity();
                                finish();
                            } else {
                                // Email is not verified
                                setStateUnconfirmed();                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, R.string.login_failed,
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().toString());
                        }
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
                    if (loginResult.isOnlyMissingConfirmation()) {
                        setStateUnconfirmed();
                    } else {
                        showLoginFailed(loginResult.getError());
                    }
                }
                if (loginResult.getSuccess() != null) {

                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    saveLogin(loginResult.getSuccess());
                    initiateAtLogin();
                    startMainActivity();
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

                return false;
            }
        });

    }

    private void initiateAtLogin() {
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void startRegisterActivity() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }


    private boolean getSavedLogin() {
        Log.d(TAG, "getSavedLogin: start");
        mAccessToken = CurrentUser.getAccessToken();
        if (mAccessToken == null) return false;
        else return true;

    }

    private void saveLogin(LoggedInUserView loggedInUserView) {
        Log.d(TAG, "saveLogin: " + loggedInUserView.getAccessToken());
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