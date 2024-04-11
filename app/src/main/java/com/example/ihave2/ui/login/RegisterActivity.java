package com.example.ihave2.ui.login;

import static com.example.ihave2.util.SharedPreferences.initiateSharedPreferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ihave2.BuildConfig;
import com.example.ihave2.MainActivity;
import com.example.ihave2.R;
import com.example.ihave2.databinding.ActivityRegisterBinding;
import com.example.ihave2.persistence.PlantRepository;
import com.example.ihave2.util.Constants;
import com.example.ihave2.util.ContextSingleton;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;
    private String mUserName;
    private String mPassword;
    private static SharedPreferences mSharedPreferences;
    private static String mMasterKeyAlias;

    // UI - components
    EditText mUsernameEditText;
    EditText mPasswordEditText;
    EditText mPasswordRepeatedEditText;
    EditText mFullNameEditText;
    EditText mEmailEditText;
    Button mRegisterButton;
    Button mConfirmButton;
    ProgressBar mLoadingProgressBar;


    private static final String TAG = "RegisterActivity";

    PlantRepository mPlantRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSingleton.getInstance(getApplicationContext());

        mPlantRepository = new PlantRepository(this);

        initiateUi();
        setListeners();
        startRegisterObservers();

        setStateRegister();


        Log.d(TAG, "onCreate: buildType: " + BuildConfig.BUILD_TYPE.toString());
        //Log.d(TAG, "onCreate: apibase: "+getResources().getString(R.string.apibase));

        //startLoginProcedure();
    }

    private void setListeners() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: register button");
                if (isRegisterValid()){
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: vi kalder viewmodel register");
                    registerViewModel.register(mUsernameEditText.getText().toString(),
                            mPasswordEditText.getText().toString(),
                            mFullNameEditText.getText().toString(),
                            mEmailEditText.getText().toString());
                }

            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean isRegisterValid() {
        return true;
    }

    private void initiateUi() {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        mUsernameEditText = binding.username;
        mPasswordEditText = binding.password;
        mPasswordRepeatedEditText = binding.passwordRepeated;
        mFullNameEditText = binding.fullName;
        mEmailEditText = binding.email;
        mRegisterButton = binding.register;
        mConfirmButton = binding.confirm;
        mLoadingProgressBar = binding.loading;
    }


    private void setStateRegister() {
        binding.username.setVisibility(View.VISIBLE);
        binding.password.setVisibility(View.VISIBLE);
        binding.passwordRepeated.setVisibility(View.VISIBLE);
        binding.fullName.setVisibility(View.VISIBLE);
        binding.email.setVisibility(View.VISIBLE);
        binding.register.setVisibility(View.VISIBLE);
        binding.unconfirmedText.setVisibility(View.GONE);
        binding.confirm.setVisibility(View.GONE);
        mConfirmButton.setEnabled(false);

    }

    private void setStateUnconfirmed() {
        binding.username.setVisibility(View.GONE);
        binding.password.setVisibility(View.GONE);
        binding.passwordRepeated.setVisibility(View.GONE);
        binding.fullName.setVisibility(View.GONE);
        binding.email.setVisibility(View.GONE);
        binding.register.setVisibility(View.GONE);
        binding.unconfirmedText.setVisibility(View.VISIBLE);
        binding.confirm.setVisibility(View.VISIBLE);
        mConfirmButton.setEnabled(true);

    }



    private boolean unconfirmedAccountCreation() {
        return false;
    }

    private void startRegisterObservers() {

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                Log.d(TAG, "onChanged: ");
                if (registerFormState == null) {
                    return;
                }
                mRegisterButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameError() != null) {
                    mUsernameEditText.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    mPasswordEditText.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getPasswordRepeatedError() != null) {
                    mPasswordRepeatedEditText.setError(getString(registerFormState.getPasswordRepeatedError()));
                }
                if (registerFormState.getFullNameError() != null) {
                    mFullNameEditText.setError(getString(registerFormState.getFullNameError()));
                }
                if (registerFormState.getEmailError() != null) {
                    mEmailEditText.setError(getString(registerFormState.getEmailError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                Log.d(TAG, "onChanged: getLoginResult ");
                if (registerResult == null) {
                    return;
                }
                mLoadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showLoginFailed(registerResult.getError());
                }
                if (registerResult.getSuccess() != null) {

                    updateUiWithUser(registerResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    setStateUnconfirmed();

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
                Log.d(TAG, "afterTextChanged: ");
                mUserName = mUsernameEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                registerViewModel.registerDataChanged(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString(),
                        mPasswordRepeatedEditText.getText().toString(),
                        mFullNameEditText.getText().toString(),
                        mEmailEditText.getText().toString());
            }
        };
        mUsernameEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordRepeatedEditText.addTextChangedListener(afterTextChangedListener);
        mFullNameEditText.addTextChangedListener(afterTextChangedListener);
        mEmailEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.register(mUsernameEditText.getText().toString(),
                            mPasswordEditText.getText().toString(),
                            mFullNameEditText.getText().toString(),
                            mEmailEditText.getText().toString());
                    }
                return false;
            }
        });

    }

    private void startMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String registered = getString(R.string.registered);

        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), registered, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}