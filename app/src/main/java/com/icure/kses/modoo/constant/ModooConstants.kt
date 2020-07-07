package com.icure.kses.modoo.constant

import java.util.*
import java.util.concurrent.Executors

object ModooConstants {

    //로그인 모드
    const val BtoB = 0
    const val BtoC = 1

    //AsyncTask 최대 스레드 개수
    val EXECUTOR = Executors.newFixedThreadPool(10)
    const val HTTP_POST = 1
    const val HTTP_GET = 2
    const val HTTP_MULTIPART = 0
    const val HTTP_CONNECTION_TIMEOUT_BASE = 10000
    const val HTTP_CONNECTION_TIMEOUT_EACH_FILE = 60000
    const val LINE_FEED = "\r\n"
    const val CHARSET_UTF_8 = "UTF-8"
    const val CHARSET_EUC_KR = "EUC-KR"
    const val TWO_HYPHEN = "--"
    val BOURDARY = String.format("%x", Random().hashCode())

    //Google Analytics event id
    const val EVENT_ID_ITEM_DETAILS_ACTIVITY_1 = "ITEM_DETAILS_ACTIVITY_001"
    const val EVENT_NAME_CART_BTN_ITEM_DETAILS_ACTIVITY = "Add to cart button clicked"
}