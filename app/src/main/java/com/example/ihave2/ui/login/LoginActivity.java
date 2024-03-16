package com.example.ihave2.ui.login;

import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.ihave2.BuildConfig;
import com.example.ihave2.MainActivity;
import com.example.ihave2.R;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.databinding.ActivityLoginBinding;
import com.example.ihave2.util.Constants;
import com.example.ihave2.util.ContextSingleton;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private String mUserName;
    private String mPassword;
    private static SharedPreferences mSharedPreferences;
    private static String mMasterKeyAlias;


    private static final String TAG = "LoginActivity";

    PlantRepository mPlantRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSingleton.getInstance(getApplicationContext());

        mPlantRepository = new PlantRepository(this);

        Log.d(TAG, "onCreate: buildType: "+ BuildConfig.BUILD_TYPE.toString());
        //Log.d(TAG, "onCreate: apibase: "+getResources().getString(R.string.apibase));

        if (!getSavedLogin()) {
            startMainActivity();
            finish();
        } else {
            if (unconfirmedAccountCreation()){
                showUnconfirmed();
            } else {
                startLoginProcedure();
            }
            startLoginProcedure();
        }


    }

    private void showUnconfirmed() {
    }

    private boolean unconfirmedAccountCreation() {
        return false;
    }

    private void startLoginProcedure() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                Log.d(TAG, "onChanged: ");
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
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
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {

                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    saveLogin();
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
                mUserName = usernameEditText.getText().toString();
                mPassword = passwordEditText.getText().toString();
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                if (usernameEditText.getText().toString().equals("d")) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            //some code here
                            Log.d(TAG, "run: visletter");
                            mPlantRepository.deleteAllPlants();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private boolean getSavedLogin() {
        mSharedPreferences = initiateSharedPreferences();
        mUserName = mSharedPreferences.getString(Constants.IHAVE_USER_NAME, null);
        mPassword = mSharedPreferences.getString(Constants.IHAVE_PASSWORD, null);
        return !(mUserName == null);
    }

    private void saveLogin() {
        mSharedPreferences = initiateSharedPreferences();
        mSharedPreferences.edit().putString(Constants.IHAVE_USER_NAME, mUserName).apply();
        mSharedPreferences.edit().putString(Constants.IHAVE_PASSWORD, mPassword).apply();
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