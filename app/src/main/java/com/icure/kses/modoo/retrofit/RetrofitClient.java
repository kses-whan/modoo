package com.icure.kses.modoo.retrofit;

import com.google.gson.GsonBuilder;
import com.icure.kses.modoo.BuildConfig;
import com.icure.kses.modoo.constant.ModooApiCodes;
import com.icure.kses.modoo.log.Log4jHelper;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Log4jHelper logger = Log4jHelper.getInstance();

    public static IModooApi modooApi;
    public static RetrofitClient retrofitClient = null;

    public static RetrofitClient getInstance(){
        if(retrofitClient == null){
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ModooApiCodes.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(createOkHttpClient())
                        .build();
                modooApi = retrofit.create(IModooApi.class);
                retrofitClient = new RetrofitClient();
            }catch (Exception e){
            }
        }

        return retrofitClient;
    }

    private static OkHttpClient createOkHttpClient(){

        OkHttpClient client = null;

        try {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {return new java.security.cert.X509Certificate[]{};}
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            client = builder
                .readTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();

        }catch(Exception e){
            logger.error("createOkHttpClient ERROR : ", e);
        }

        return client;
    }
}
