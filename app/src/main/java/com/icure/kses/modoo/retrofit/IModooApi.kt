package com.icure.kses.modoo.retrofit

import android.util.Log
import com.google.gson.GsonBuilder
import com.icure.kses.modoo.BuildConfig
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.log.Log4jHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface IModooApi {

    //    @Multipart
    //    @POST(SMARTLOG_MOBILE + UPLOADFILE)
    //    Call<ResponseBody> uploadFileInfo(@PartMap HashMap<String, RequestBody> field, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST(ModooApiCodes.API_BASE_URL + URL_TEST)
    fun testRequest(@FieldMap field: Map<String, String>): Call<ResponseBody>

    @GET(ModooApiCodes.API_BASE_URL + URL_TEST)
    fun testRequest(@Query("userid") userId: String,
                    @Query("userpw") userPw: String): Call<ResponseBody>

    companion object {
        val logger: Log4jHelper = Log4jHelper.getInstance();

        const val URL_TEST = ""

        // URLs...
        //로그인
        const val URL_LOGIN = ""

        //회원가입
        const val URL_SIGNIN = ""

        //ID/비번 찾기
        const val URL_RESTORE_ACCOUNT = ""

        //회원탈퇴
        const val URL_RESIGN = ""

        //이용약관 / 개인정보처리방침 조회
        const val URL_TERMS = ""

        //FAQ 조회 (공지사항 + 구독서비스 이용안내 + 주문/결제 안내 + 배송문의 + 반품/교환/환불 안내 + 기타문의 + 자주묻는질문리스트)
        const val URL_FAQ = ""

        //검색
        const val URL_SEARCH = ""

        //상품 리스트 조회 (PICK + BEST)
        const val URL_PRODUCT_LIST = ""

        //상품 카테고리 & 브랜드 리스트 조회
        const val URL_CATEGORY_LIST = ""

        //상품 상세 (상품리뷰 리스트 + Q&A + 교환/반품 안내 포함) 조회
        const val URL_PRODUCT_DETAIL = ""

        //상품리뷰 상세 조회
        const val URL_REVIEW_DETAIL = ""

        //리뷰 추천 / 추천취소
        const val URL_REVIEW_RECOMMEND = ""

        //문의하기 / 문의수정 (Q&A)
        const val URL_QA_UPDATE = ""

        //장바구니 담기
        const val URL_ADD_CART = ""

        //장바구니 조회
        const val URL_CART_LIST = ""

        //주문/결제 화면 조회
        const val URL_PAYMENT_DETAIL = ""

        //결제
        const val URL_PAYMENT = ""

        //구독관리
        //구독관리 리스트 조회 (구독중 + 구독해지 + 구독종료 + 구독중지)
        const val URL_MYSUBS_LIST = ""

        //구독 상세 조회 (결제정보 + 배송지정보 + 배송현황 리스트)
        const val URL_MYSUBS_DETAIL = ""

        //구독 변경
        const val URL_MYSUBS_ADJUST = ""

        //상품평 쓰기
        const val URL_MYSUBS_REVIEW = ""

        //구독 중지
        const val URL_STOP_SUBS = ""

        //구독 해지
        const val URL_CANCEL_SUBS = ""

        //배송
        //배송지 리스트 조회
        const val URL_DELIVERY_LIST = ""

        //배송지입력
        const val URL_DELIVERY = ""

        //배송추적
        const val URL_DEVLIVERY_TRACK = ""

        //우편번호 찾기
        const val URL_SEARCH_POST = ""

        //교환/환불
        //교환/환불 리스트 조회
        const val URL_REFUND_LIST = ""

        //교환/환불 상세
        const val URL_REFUND_DETAIL = ""

        //게시판
        //내 상품평 리스트 조회
        const val URL_MYSUBS_REVIEW_LIST = ""

        //Q&A 리스트 조회
        const val URL_MYSUBS_QA_LIST = ""

        //내정보 조회
        const val URL_MYINFO = ""

        var instance: IModooApi

        init {
            instance = Retrofit.Builder()
                    .baseUrl(ModooApiCodes.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(createOkHttpClient())
                    .build()
                    .create(IModooApi::class.java)
        }

        private fun createOkHttpClient(): OkHttpClient? {
            var client: OkHttpClient? = null
            try {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                if (BuildConfig.DEBUG) {
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
                }

                val trustAllCerts = arrayOf<TrustManager>(
                        object : X509TrustManager {
                            @Throws(CertificateException::class)
                            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                            @Throws(CertificateException::class)
                            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                            override fun getAcceptedIssuers(): Array<X509Certificate> {
                                return arrayOf()
                            }
                        }
                )
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                val sslSocketFactory = sslContext.socketFactory
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
                builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

                val headerInterceptor = Interceptor {
                    val request = it.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                    return@Interceptor it.proceed(request)
                }

                client = builder
                        .readTimeout(20, TimeUnit.SECONDS)
                        .addInterceptor(headerInterceptor)
                        .addNetworkInterceptor(httpLoggingInterceptor)
                        .build()

            } catch (e: Exception) {
                logger.error("createOkHttpClient ERROR : ", e)
            }
            return client
        }

    }
}