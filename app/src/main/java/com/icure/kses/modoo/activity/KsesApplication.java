package com.icure.kses.modoo.activity;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by whan 2020-06-02
 */
public class KsesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 카카오 SDK 초기화
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return KsesApplication.this;
                    }
                };
            }
        });
    }

}
