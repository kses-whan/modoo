package com.icure.kses.modoo.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.icure.kses.modoo.R
import com.icure.kses.modoo.database.ModooDatabaseHelper
import com.icure.kses.modoo.log.Log4jHelper
import com.icure.kses.modoo.permission.ModooPermissionHelper
import com.icure.kses.modoo.push.FCMManager

class ModooSplashActivity : Activity(), AnimationListener {

    var animFadeIn: Animation? = null
    var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //로그 초기화
        Log4jHelper.nullInstance()

        //위변조 검사

        val incomingIntent = intent
        if (incomingIntent != null && incomingIntent.getStringExtra("msg") != null) {
            Log.i("tagg", "incomingIntent data : " + incomingIntent.getStringExtra("msg"))
        }

        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions

        animFadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.animation_fade_in)
        animFadeIn?.setAnimationListener(this)
        linearLayout = findViewById<View>(R.id.layout_linear) as LinearLayout
        linearLayout?.visibility = View.VISIBLE
        linearLayout?.startAnimation(animFadeIn)

        // DB 초기화
//        ModooDatabaseHelper.getInstance(this)

        // Push 초기화
        initFCM()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onAnimationStart(animation: Animation) {
        //under Implementation
    }

    override fun onAnimationEnd(animation: Animation) {
        if (ModooPermissionHelper.checkPermissions(this)) {
            val i = Intent(this@ModooSplashActivity, ModooWelcomeActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onAnimationRepeat(animation: Animation) {}
    private fun initFCM() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FCMManager(applicationContext)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        ModooPermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}