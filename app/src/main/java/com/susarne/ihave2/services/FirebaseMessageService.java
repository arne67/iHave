package com.susarne.ihave2.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.models.AppTokenDto;
import com.susarne.ihave2.models.PlantWithListsDto;
import com.susarne.ihave2.util.CurrentUser;
import com.susarne.ihave2.util.PlantUpdater;
import com.susarne.ihave2.util.Worker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    FirebaseMessaging firebaseMessaging;
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        updateAppTokenAsync(token);

        // Send token til din server
        //sendRegistrationToServer(token);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Håndter beskeden her
        if (remoteMessage.getData().size() > 0) {
            // Beskeddata
            String action = remoteMessage.getData().get("action");
            Log.d(TAG, "onMessageReceived: action"+action);
            if ("get_updated".equals(action)) {
                Log.d(TAG, "onMessageReceived: vi henter");
                //Worker.getNewUpdatedPlants();
                PlantUpdater plantUpdater = new PlantUpdater();
                plantUpdater.getUpdatedPlants();

                // Kode til at hente nye oplysninger fra serveren
            }
        }
    }

    public void fetchTokenManually() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "Fetched token manually: " + token);
                    updateAppTokenAsync(token);
                });
    }

    private void updateAppTokenAsync(String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                updateAppToken(token);
            }

        });
        t.start();

    }
    private void updateAppToken(String token) {
        AppTokenDto appTokenDto = new AppTokenDto();
        appTokenDto.setAppToken(token);
        String authToken = "";
        Call<AppTokenDto> call = PlantApiClient.getInstance().getMyApi().updateAppToken(authToken, CurrentUser.getUserId(),appTokenDto);
        try {
            Response<AppTokenDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "updateAppToken success: #a ");
            } else {
                // handle unsuccessful response
                Log.d(TAG, "updateAppToken: a# fejl" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "updateAppToken: io-fejl");
        }
    }
}