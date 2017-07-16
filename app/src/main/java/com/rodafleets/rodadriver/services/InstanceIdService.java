package com.rodafleets.rodadriver.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

public class InstanceIdService extends FirebaseInstanceIdService {

//    public void getToken() {
//        // Get InstanceID token.
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.i(AppConstants.APP_NAME, "Token: " + token);
//    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(AppConstants.APP_NAME, "Refreshed token: " + refreshedToken);
        ApplicationSettings.setCloudMessagingId(this, refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        updateRegistrationId(refreshedToken);
    }
}
