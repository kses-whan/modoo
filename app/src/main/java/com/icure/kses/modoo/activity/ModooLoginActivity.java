package com.icure.kses.modoo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.icure.kses.modoo.R;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import static com.icure.kses.modoo.activity.ModooSettingsActivity.PREF_AUTO_LOGIN;

public class ModooLoginActivity extends AppCompatActivity {

    boolean isAutoLogin = false;

    // 구글 로그인
    private TextView mSignInButton = null;
    private GoogleSignInClient mGoogleSignInClient = null;
    private FirebaseAuth mAuth = null;
    private int RC_SIGN_IN = 0x0011;

    // 카카오 로그인 세션 콜백 구현
    private ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            getKakaoAcountInfo();
            startMainActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }
    };

    private void startMainActivity(){
        Intent toMainIntent = new Intent(ModooLoginActivity.this, ModooMainActivity.class);
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toMainIntent);
        finish();
    }

    private void getKakaoAcountInfo(){
        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {}

                    @Override
                    public void onFailure(ErrorResult errorResult) {}

                    @Override
                    public void onSuccess(MeV2Response result) {
                        UserAccount kakaoAccount = result.getKakaoAccount();
                        if (kakaoAccount != null) {

                            // 이메일
                            String email = kakaoAccount.getEmail();

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
                            Profile profile = kakaoAccount.getProfile();

                            if (profile != null) {

                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }
                        } else {
                            Log.i("tagg","kakao account info is null!!");
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modoo_login);

        //자동로그인 확인
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isAutoLogin = prefs.getBoolean(PREF_AUTO_LOGIN, false);

        // 카카오 로그인 세션 콜백 등록
        Session.getCurrentSession().addCallback(sessionCallback);
        if(isAutoLogin) {
            if (Session.getCurrentSession().checkAndImplicitOpen()) {
                return;
            }
        }

        // 구글 로그인
        mSignInButton = findViewById(R.id.btn_login_google);
        startGoggleLogin();

        if(!isAutoLogin)
            logoutAll();
    }


    private void startGoggleLogin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        if(isAutoLogin) {
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signInAccount != null) {

                mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            return;
                        }

                    }
                });
            }
        }

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGoogleLogin();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
        }
    }


    private void doGoogleLogin(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // 구글 로그인 결과
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            if (mAuth == null) return;
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
//                                Log.i("tagg", "google account info : " + user.getEmail());
//                                Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                                startMainActivity();
                            } else {
//                                Log.w("tagg", "signInWithCredential:failure", task.getException());
//                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Log.e("tagg","ERROR : " + e.getMessage());
        }
    }


    private void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private void logoutAll(){
        UserManagement.getInstance()
                .requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
//                        Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNotSignedUp() {
//                        Toast.makeText(getApplicationContext(), "onNotSignedUp", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
//                        Toast.makeText(getApplicationContext(), "Kakao Session Closed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(Long result) {
//                        Toast.makeText(getApplicationContext(), "Kakao Logout Complete", Toast.LENGTH_LONG).show();
                    }
                });

        FirebaseAuth.getInstance().signOut();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .build();
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.i("tagg", "구글 로그아웃 완료");
//            }
//        });

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Google Logout Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

}
