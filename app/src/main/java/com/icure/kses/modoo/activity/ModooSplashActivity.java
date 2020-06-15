package com.icure.kses.modoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.icure.kses.modoo.R;
import com.icure.kses.modoo.database.Modoo_DatabaseHelper;
import com.icure.kses.modoo.log.Log4jHelper;
import com.icure.kses.modoo.push.FCMManager;

public class ModooSplashActivity extends Activity implements Animation.AnimationListener {

    Animation animFadeIn;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent incomingIntent = getIntent();
        if(incomingIntent != null && incomingIntent.getStringExtra("msg") != null){
            Log.i("tagg", "incomingIntent data : " + incomingIntent.getStringExtra("msg") );
        }

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
        }
        // load the animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_fade_in);
        // set animation listener
        animFadeIn.setAnimationListener(this);
        // animation for image
        linearLayout = (LinearLayout) findViewById(R.id.layout_linear);
        // start the animation
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.startAnimation(animFadeIn);

        Log4jHelper.nullInstance();
        new Modoo_DatabaseHelper(this);
        initFCM();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //under Implementation
    }

    public void onAnimationEnd(Animation animation) {
            // Start Main Screen
            Intent i = new Intent(ModooSplashActivity.this, ModooWelcomeActivity.class);
            startActivity(i);
            this.finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //under Implementation
    }

    private void initFCM(){
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        new FCMManager(this);
    }

}