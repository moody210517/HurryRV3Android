package com.hurry.custom.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.hurry.custom.common.db.PreferenceUtils;


/**
 * Created by Administrator on 6/7/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {



    @Override
    public void onNewToken(String token) {


        PreferenceUtils.setDeviceToken(this, token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}
