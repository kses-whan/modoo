package com.icure.kses.modoo.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.icure.kses.modoo.utility.PrefManager;

public class FCMManager {

    protected PrefManager mPrefManager = null;

    public FCMManager(Context context){
        mPrefManager = new PrefManager(context);
        init(context);
    }

    private void init(Context context){
        if(TextUtils.isEmpty(mPrefManager.getFcmAccessToken())){
            registerInBackground(context);
        }
    }

    public boolean checkPlayServices(Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            //구글계정설정 안내
            return false;
        }
        return true;
    }

    private void registerInBackground(Context context) {
        if(!checkPlayServices(context)){
            // error return
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String accessToken = instanceIdResult.getToken();
                Log.i("tagg","accessToken : " + accessToken);

                mPrefManager.setFcmAccessToken(accessToken);

                //서버로 보냄
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // fcm access token error
            }
        });
    }

}
