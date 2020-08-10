package com.icure.kses.modoo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.icure.kses.modoo.R
import com.icure.kses.modoo.config.ModooConfig
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.constant.ModooConstants
import com.icure.kses.modoo.model.ModooViewModel
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.OptionalBoolean
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*

class ModooLoginActivity : AppCompatActivity() {
    var isAutoLogin = false

    // 구글 로그인
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 0x0011

    // 카카오 로그인 세션 콜백 구현
    private val sessionCallback: ISessionCallback = object : ISessionCallback {
        override fun onSessionOpened() {
            kakaoAcountInfo
            startMainActivity()
        }

        override fun onSessionOpenFailed(exception: KakaoException) {}
    }

    // 이메일
    private val kakaoAcountInfo: Unit
        private get() {
            UserManagement.getInstance()
                    .me(object : MeV2ResponseCallback() {
                        override fun onSessionClosed(errorResult: ErrorResult) {}
                        override fun onFailure(errorResult: ErrorResult) {}
                        override fun onSuccess(result: MeV2Response) {
                            val kakaoAccount = result.kakaoAccount
                            if (kakaoAccount != null) {

                                // 이메일
                                val email = kakaoAccount.email
                                if (email != null) {
                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 이메일 획득 가능
                                    // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
//                                List<String> scopeList = new ArrayList<String>();
//                                scopeList.add("account_email");
//                                Session.getCurrentSession().updateScopes(ModooLoginActivity.this, scopeList, null);
                                } else {
                                    // 이메일 획득 불가
                                }

                                // 프로필
                                val profile = kakaoAccount.profile
                                if (profile != null) {
                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 프로필 정보 획득 가능
                                } else {
                                    // 프로필 획득 불가
                                }
                            } else {
                                Log.i("tagg", "kakao account info is null!!")
                            }
                        }
                    })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //자동로그인 여부 확인
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        isAutoLogin = prefs.getBoolean(ModooSettingsActivity.PREF_AUTO_LOGIN, false)

        // 현재 로그인 상태에 따라 분기처리
        when(ModooConfig.loginMode){
            //BtoB (이지웰)
            ModooConstants.BtoB -> {
                ll_login_container_ezwel.visibility = View.VISIBLE
                ll_login_container_btc.visibility = View.GONE
                startBtoBLoginProcess()
            }
            //BtoC
            ModooConstants.BtoC -> {
                ll_login_container_ezwel.visibility = View.GONE
                ll_login_container_btc.visibility = View.VISIBLE
                startBtoCLoginProcess()
            }
            else -> {
                //기본은 BtoC
                ll_login_container_ezwel.visibility = View.GONE
                ll_login_container_btc.visibility = View.VISIBLE
            }
        }

        btn_login_login.setOnClickListener {
            reqLogin()
        }
    }

    private fun startBtoBLoginProcess(){
        // to do...
    }

    private fun startBtoCLoginProcess(){
        // 카카오 로그인 세션 콜백 등록
        Session.getCurrentSession().addCallback(sessionCallback)
        if (isAutoLogin) {
            if (Session.getCurrentSession().checkAndImplicitOpen()) {
                return
            }
        }

        // 구글 로그인
        startGoggleLogin()

        //자동로그인이 아니면 전부 로그아웃 처리한다.
        if (!isAutoLogin) logoutAll()
    }

    private fun startGoggleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        if (isAutoLogin) {
            val signInAccount = GoogleSignIn.getLastSignedInAccount(this)
            signInAccount?.let {
                mGoogleSignInClient?.silentSignIn()?.addOnCompleteListener(this, OnCompleteListener { task ->
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    return@OnCompleteListener
                }
            }) }
        }
        btn_login_google?.setOnClickListener { doGoogleLogin() }
    }

    public override fun onStart() {
        super.onStart()
        mAuth?.let {
            val currentUser = it.currentUser
        }
    }

    private fun doGoogleLogin() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        // 구글 로그인 결과
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        try {
            val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
            mAuth?.let {
                it.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth?.let { it.currentUser }
//                                Log.i("tagg", "google account info : " + user.getEmail());
//                                Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                            startMainActivity()
                        } else {
//                                Log.w("tagg", "signInWithCredential:failure", task.getException());
//                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("tagg", "ERROR : " + e.message)
        }
    }

    private fun signOut() {
        mAuth?.let { it.signOut() }
        mGoogleSignInClient?.let {
            it.signOut().addOnCompleteListener(this) {
            //Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth?.let {it.signOut()}
        // Google revoke access
        mGoogleSignInClient?.let {
            it.revokeAccess().addOnCompleteListener(this) {
            //Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun logoutAll() {
        // 카카오톡 로그아웃
        UserManagement.getInstance()
                .requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
//                        Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_LONG).show();
                    }

                    override fun onNotSignedUp() {
//                        Toast.makeText(getApplicationContext(), "onNotSignedUp", Toast.LENGTH_LONG).show();
                    }

                    override fun onSessionClosed(errorResult: ErrorResult) {
//                        Toast.makeText(getApplicationContext(), "Kakao Session Closed", Toast.LENGTH_LONG).show();
                    }

                    override fun onSuccess(result: Long) {
//                        Toast.makeText(getApplicationContext(), "Kakao Logout Complete", Toast.LENGTH_LONG).show();
                    }
                })
        FirebaseAuth.getInstance().signOut()

        //구글 로그아웃
        mGoogleSignInClient?.let {
            it.signOut().addOnCompleteListener(this) {
            //Toast.makeText(getApplicationContext(), "Google Logout Complete", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun reqLogin(){
        val parameter = HashMap<String, String>()
        parameter.put("userid",et_login_ezwel_id.text.toString())
        parameter.put("userpw",et_login_ezwel_id.text.toString())
        parameter.put("companycode",et_login_ezwel_id.text.toString())
        parameter.put("staff",et_login_ezwel_id.text.toString())
        parameter.put("userid",et_login_ezwel_id.text.toString())

        val mvm = ViewModelProviders.of(this@ModooLoginActivity).get(ModooViewModel::class.java)
        mvm.getLoginData(parameter)?.observe(this, Observer {
            when(it.resultCode){
                ModooApiCodes.API_RETURNCODE_SUCCESS -> {
                    startTermsActivity()
                }
                else -> Toast.makeText(this, R.string.http_result_msg_error_e999, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startMainActivity() {
        val toMainIntent = Intent(this@ModooLoginActivity, ModooHomeActivity::class.java)
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(toMainIntent)
        finish()
    }

    private fun startTermsActivity() {
        val toMainIntent = Intent(this@ModooLoginActivity, ModooTermsActivity::class.java)
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(toMainIntent)
        finish()
    }
}