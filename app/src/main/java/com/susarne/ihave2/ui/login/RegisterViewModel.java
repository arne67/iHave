package com.susarne.ihave2.ui.login;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.susarne.ihave2.R;
import com.susarne.ihave2.data.LoginRepository;
import com.susarne.ihave2.data.Result;
import com.susarne.ihave2.data.model.LoggedInUser;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private static final String TAG = "RegisterViewModel";

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String userName, String password, String fullName, String email) {
        // can be launched in a separate asynchronous job
        Log.d(TAG, "register: Thread: "+Thread.currentThread().getName());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Thread: " + Thread.currentThread().getName());
                Log.d(TAG, "run: vi kalder repository register");
                Result<LoggedInUser> result = loginRepository.register(userName, password,fullName,email);

                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    registerResult.postValue(new RegisterResult(new LoggedInUserView(data.getName(),data.getUserId(),data.getAccessToken())));
                } else {
                    Result.Error resultError = (Result.Error) result;
                    if (resultError.getResponseCode()==null) {
                        registerResult.postValue(new RegisterResult(R.string.register_failed));
                    } else if (resultError.getResponseCode()==409) {
                        registerResult.postValue(new RegisterResult(R.string.register_duplicate));
                    } else {
                        registerResult.postValue(new RegisterResult(R.string.register_failed));
                    }

                }
            }
        });
        t.start();
    }

    public void registerDataChanged(String username, String password, String passwordRepeated,String fullName,String email) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null,null,null,null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password,null,null,null));
        } else if (!isPasswordRepeatedValid(password,passwordRepeated)) {
            registerFormState.setValue(new RegisterFormState(null, null,R.string.invalid_password_repeated,null,null));
        } else if (!isFullNameValid(fullName)) {
            registerFormState.setValue(new RegisterFormState(null, null,null,R.string.invalid_fullname,null));
        } else if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(null, null,null,null,R.string.invalid_email));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
    private boolean isPasswordRepeatedValid(String password, String passwordRepeated) {
        return password.equals(passwordRepeated);
    }
    private boolean isFullNameValid(String fullName) {
        if (fullName == null) {
            return false;
        }
        if (fullName.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }

    }
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}