package com.icure.kses.modoo.constant;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModooConstants {
    //AsyncTask 최대 스레드 개수
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    public static final int HTTP_POST = 1;
    public static final int HTTP_GET = 2;
    public static final int HTTP_MULTIPART = 0;

    public static final int HTTP_CONNECTION_TIMEOUT_BASE = 10000;
    public static final int HTTP_CONNECTION_TIMEOUT_EACH_FILE = 60000;

    private static final String LINE_FEED = "\r\n";
    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String CHARSET_EUC_KR = "EUC-KR";
    private static final String TWO_HYPHEN = "--";
    private final static String BOURDARY = String.format("%x", new Random().hashCode());

    //Google Analytics event id
    public static final String EVENT_ID_ITEM_DETAILS_ACTIVITY_1 = "ITEM_DETAILS_ACTIVITY_001";
    public static final String EVENT_NAME_CART_BTN_ITEM_DETAILS_ACTIVITY = "Add to cart button clicked";
}
