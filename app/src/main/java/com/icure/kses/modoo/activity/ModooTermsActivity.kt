package com.icure.kses.modoo.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.icure.kses.modoo.R
import kotlinx.android.synthetic.main.activity_terms.*

class ModooTermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        //타이틀지정
        supportActionBar?.setTitle(R.string.layout_terms_title_agree)

        //초기화
        layoutInit()
    }

    private fun layoutInit(){
        tv_terms_agree1.movementMethod = ScrollingMovementMethod()
        tv_terms_agree2.movementMethod = ScrollingMovementMethod()
        tv_terms_agree3.movementMethod = ScrollingMovementMethod()

        btn_terms_signin.setOnClickListener {
            startMainActivity();
        }

        cb_terms_agreeall.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                rg_terms_agree1.check(R.id.cb_terms_agree1)
                rg_terms_agree2.check(R.id.cb_terms_agree2)
                rg_terms_agree3.check(R.id.cb_terms_agree3)
            } else {
                rg_terms_agree1.clearCheck()
                rg_terms_agree2.clearCheck()
                rg_terms_agree3.clearCheck()
            }
        }
    }

    private fun startMainActivity() {
        val toMainIntent = Intent(this@ModooTermsActivity, ModooMainActivity::class.java)
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(toMainIntent)
        finish()
    }
}