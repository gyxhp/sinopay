package com.sinosoft.pay;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description: 支付核心服务,包括:各支付渠道接口,通知处理
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SinoPayServiceApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SinoPayServiceApplication.class).web(true).run(args);
	}

}
