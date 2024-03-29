package com.example.ihave2.data;

import android.util.Log;

import com.example.ihave2.api.PlantApiClient;
import com.example.ihave2.data.model.LoggedInUser;
import com.example.ihave2.models.LoginRequestDto;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static final String TAG = "LoginDataSource";

    public Result<LoggedInUser> login(String userName, String password) {
        Log.d(TAG, "login: Thread: "+Thread.currentThread().getName());
        LoginRequestDto loginRequestDto = new LoginRequestDto(userName,password);

        Call<LoggedInUser> call = PlantApiClient.getInstance().getMyApi().login("1",loginRequestDto);
        try {
            Response<LoggedInUser> response = call.execute();
            if (response.code()==200) {
                return new Result.Success<>(response.body());
            } else {
                // handle unsuccessful response
                return new Result.Error(new IOException("User not found"));
            }
        } catch (IOException e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

//        try {
//            // TODO: handle loggedInUser authentication
//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe");
//            return new Result.Success<>(fakeUser);
//        } catch (Exception e) {
//            return new Result.Error(new IOException("Error logging in", e));
//        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}