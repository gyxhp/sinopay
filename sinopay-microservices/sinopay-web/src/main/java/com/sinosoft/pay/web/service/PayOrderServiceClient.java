package com.sinosoft.pay.web.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.sinosoft.pay.common.util.MyBase64;

/**
 * @Description:
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@Service
public class PayOrderServiceClient {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 创建支付订单
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "createPayOrderFallback")
    public String createPayOrder(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/create?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String createPayOrderFallback(String jsonParam) {
        return "error";
    }

    /**
     * 查询支付订单
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "queryPayOrderFallback")
    public String queryPayOrder(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/query?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String queryPayOrderFallback(String jsonParam) {
        return "error";
    }

    /**
     * 处理微信支付
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "doWxPayReqFallback")
    public String doWxPayReq(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/channel/wx?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String doWxPayReqFallback(String jsonParam) {
        return "error";
    }

    /**
     * 处理支付宝wap支付
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "doAliPayWapReqFallback")
    public String doAliPayWapReq(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/channel/ali_wap?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String doAliPayWapReqFallback(String jsonParam) {
        return "error";
    }

    /**
     * 处理支付宝即时到账支付
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "doAliPayPcReqFallback")
    public String doAliPayPcReq(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/channel/ali_pc?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String doAliPayPcReqFallback(String jsonParam) {
        return "error";
    }

    /**
     * 处理支付宝手机支付
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "doAliPayMobileReqFallback")
    public String doAliPayMobileReq(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/channel/ali_mobile?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String doAliPayMobileReqFallback(String jsonParam) {
        return "error";
    }

    /**
     * 处理支付宝当面付扫码支付
     * @param jsonParam
     * @return
     */
    @HystrixCommand(fallbackMethod = "doAliPayQrReqFallback")
    public String doAliPayQrReq(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/pay/channel/ali_qr?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String doAliPayQrReqFallback(String jsonParam) {
        return "error";
    }

}