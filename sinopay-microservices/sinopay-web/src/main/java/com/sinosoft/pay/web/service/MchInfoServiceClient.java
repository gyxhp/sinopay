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
public class MchInfoServiceClient {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "selectMchInfoFallback")
    public String selectMchInfo(String jsonParam) {
        return restTemplate.getForEntity("http://SINOPAY-SERVICE/mch_info/select?jsonParam=" + MyBase64.encode(jsonParam.getBytes()), String.class).getBody();
    }

    public String selectMchInfoFallback(String jsonParam) {
        return "error";
    }

}