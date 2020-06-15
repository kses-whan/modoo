package com.icure.kses.modoo.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by whan 2020/06/08
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences constants
    private static final String PREF_NAME = "modoopref";

    private static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";
    private static final String FCM_ACCESS_TOKEN = "FCM_ACCESS_TOKEN";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFcmAccessToken(String accessToken){
        editor.putString(FCM_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public String getFcmAccessToken(){
        return pref.getString(FCM_ACCESS_TOKEN, null);
    }
}
