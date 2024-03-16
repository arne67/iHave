package com.example.ihave2.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.example.ihave2.data.LoginRepository;
import com.example.ihave2.data.Result;
import com.example.ihave2.data.model.LoggedInUser;
import com.example.ihave2.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private static final String TAG = "LoginViewModel";

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String userName, String password) {
        // can be launched in a separate asynchronous job
        Log.d(TAG, "login: Thread: "+Thread.currentThread().getName());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Thread: " + Thread.currentThread().getName());
                Result<LoggedInUser> result = loginRepository.login(userName, password);

                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    //loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data.getName())));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
        t.start();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
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
        return password != null && password.trim().length() > 0;
    }
}