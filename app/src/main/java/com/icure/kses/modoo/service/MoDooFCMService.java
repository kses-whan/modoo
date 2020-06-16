package com.icure.kses.modoo.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MoDooFCMService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            Log.i("tagg", "From: " + remoteMessage.getFrom() + ", remoteMessage size : " + remoteMessage.getData().size());

            RemoteMessage.Notification notification = remoteMessage.getNotification();

            if(notification != null){
//                Log.i("tagg","push msg received : " + data);
            }

            Map<String, String> data = remoteMessage.getData();
//            String msg = data.get("msg");

            Log.i("tagg","push msg received : " + data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
