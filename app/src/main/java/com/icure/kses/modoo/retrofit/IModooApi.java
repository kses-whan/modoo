package com.icure.kses.modoo.retrofit;

import com.icure.kses.modoo.constant.ModooApiCodes;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IModooApi {

    final String URL_TEST_GET = "";

    final String URL_TEST_POST = "";

    // URLs...
    //로그인
    final String URL_LOGIN = "";
    //회원가입
    final String URL_SIGNIN = "";
    //ID/비번 찾기
    final String URL_RESTORE_ACCOUNT = "";
    //회원탈퇴
    final String URL_RESIGN = "";
    //이용약관 / 개인정보처리방침 조회
    final String URL_TERMS = "";
    //FAQ 조회 (공지사항 + 구독서비스 이용안내 + 주문/결제 안내 + 배송문의 + 반품/교환/환불 안내 + 기타문의 + 자주묻는질문리스트)
    final String URL_FAQ = "";
    //검색
    final String URL_SEARCH = "";
    //상품 리스트 조회 (PICK + BEST)
    final String URL_PRODUCT_LIST = "";
    //상품 카테고리 & 브랜드 리스트 조회
    final String URL_CATEGORY_LIST = "";
    //상품 상세 (상품리뷰 리스트 + Q&A + 교환/반품 안내 포함) 조회
    final String URL_PRODUCT_DETAIL = "";
    //상품리뷰 상세 조회
    final String URL_REVIEW_DETAIL = "";
    //리뷰 추천 / 추천취소
    final String URL_REVIEW_RECOMMEND = "";
    //문의하기 / 문의수정 (Q&A)
    final String URL_QA_UPDATE = "";
    //장바구니 담기
    final String URL_ADD_CART = "";
    //장바구니 조회
    final String URL_CART_LIST = "";
    //주문/결제 화면 조회
    final String URL_PAYMENT_DETAIL = "";
    //결제
    final String URL_PAYMENT = "";

    //구독관리
    //구독관리 리스트 조회 (구독중 + 구독해지 + 구독종료 + 구독중지)
    final String URL_MYSUBS_LIST = "";
    //구독 상세 조회 (결제정보 + 배송지정보 + 배송현황 리스트)
    final String URL_MYSUBS_DETAIL = "";
    //구독 변경
    final String URL_MYSUBS_ADJUST = "";
    //상품평 쓰기
    final String URL_MYSUBS_REVIEW = "";
    //구독 중지
    final String URL_STOP_SUBS = "";
    //구독 해지
    final String URL_CANCEL_SUBS = "";
    //배송
    //배송지 리스트 조회
    final String URL_DELIVERY_LIST = "";
    //배송지입력
    final String URL_DELIVERY = "";
    //배송추적
    final String URL_DEVLIVERY_TRACK = "";
    //우편번호 찾기
    final String URL_SEARCH_POST = "";

    //교환/환불
    //교환/환불 리스트 조회
    final String URL_REFUND_LIST = "";
    //교환/환불 상세
    final String URL_REFUND_DETAIL = "";

    //게시판
    //내 상품평 리스트 조회
    final String URL_MYSUBS_REVIEW_LIST = "";
    //Q&A 리스트 조회
    final String URL_MYSUBS_QA_LIST= "";

    //내정보 조회
    final String URL_MYINFO = "";

//    @Multipart
//    @POST(SMARTLOG_MOBILE + UPLOADFILE)
//    Call<ResponseBody> uploadFileInfo(@PartMap HashMap<String, RequestBody> field, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST(ModooApiCodes.API_BASE_URL + URL_TEST_POST)
    Call<ResponseBody> testRequest(@FieldMap HashMap<String, RequestBody> field);

    @GET(ModooApiCodes.API_BASE_URL + URL_TEST_GET)
    Call<ResponseBody> testRequest(@Query("userid") String userId,
                                @Query("userpw") String userPw);

}
