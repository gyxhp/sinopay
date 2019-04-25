package com.sinosoft.pay.shop.util;


public class Constant {

    public static final Byte GOODS_ORDER_STATUS_INIT = 0;
    public static final Byte GOODS_ORDER_STATUS_SUCCESS = 1;
    public static final Byte GOODS_ORDER_STATUS_COMPLETE = 2;
    public static final Byte GOODS_ORDER_STATUS_FAIL = -1;

    public static final String mchId = "10000000";
    // 加签key
    public static final String reqKey = "jieshuntest123!@#";
    // 验签key
    public static final String resKey = "jieshuntest123!@#";

    public static final String baseUrl = "https://pay.shanghailife.com.cn/api";
    public static final String notifyUrl = "https://pay.shanghailife.com.cn/sinopay/pay/payNotify";
    public final static String QR_PAY_URL = "https://pay.shanghailife.com.cn/sinopay/pay/qrPay.html";
    public static final String AppID = "wxa997557da13faff4";
    public static final String AppSecret = "6a10142df4515dcc6cb63b239d7f5835";
    public final static String GetOpenIdURL = "localhost:8181/sinopay/pay/getOpenId";
    public final static String GetOpenIdURL2 = "https://pay.shanghailife.com.cn/sinopay/pay/getOpenId2";
    //支付的地址，用于微信获取openid后重新返回来
    public final static String PAY_URL = "localhost:8181/sinopay/wechatpay/paymoney";
    public final static String QUR_URL = "";
}
