package com.sinosoft.pay.shop.demo;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.pay.common.util.SinoPayUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MgrDemo {

    // 商户ID

    static final String baseUrl = "http://localhost:8080";

    public static void main(String[] args) {
        //addMchInfo();
//       addPayChannel();
        try {
            String encode = URLEncoder.encode("{ \"mchId\": \"10000000\", \"notifyUrl\": \"http://www.baidu.com\", \"amount\": 1, \"subject\": \"sinopay_支付测试 \", \"body\": \"sinopay_支付测试 \", \"succReturnUrl\": \"http://www.taobao.com\", \"failReturnUrl\": \"http://www.jd.com\" }", "utf-8");
            System.out.println(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 添加商户
    static void addMchInfo() throws Exception{
        JSONObject params = new JSONObject();
        params.put("mchId", "20001226");
        params.put("name", "百年树丁");
        params.put("type", "1");
        params.put("reqKey", "298332323231231313");
        params.put("resKey", "883435353534543534");
        String reqData = "params=" + params.toJSONString();
        System.out.println("请求支付中心添加商户接口,请求数据:" + reqData);
        String url = baseUrl + "/mch/add?";
        String result = SinoPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心添加商户接口,响应数据:" + result);

    }

    // 添加渠道
    static void addPayChannel() throws Exception{
        JSONObject params = new JSONObject();
        params.put("channelId", "WX_NATIVE");//WX_NATIVE
        params.put("channelName", "WX");//WX
        params.put("channelMchId", "1481721182");
        params.put("mchId", "20001223");
        params.put("param","{\"mchId\":\"1481721182\", \"appId\":\"wx077cb62e341f8a5c\", \"key\":\"***\", \"certLocalPath\":\"wx/1481721182_cert.p12\", \"certPassword\":\"1481721182\", \"desc\":\"sinopay_shop-native(sinopay扫码支付)\"}");
        params.put("remark", "微信扫码支付");
        String reqData = "params=" + URLEncoder.encode(params.toJSONString(),"utf-8");
        System.out.println("请求支付中心添加渠道接口,请求数据:" + reqData);
        String url = baseUrl + "/pay_channel/save?";
        String result = SinoPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心添加渠道接口,响应数据:" + result);
    }


}
