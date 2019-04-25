package com.sinosoft.pay.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Description: 服务注册中心
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@EnableEurekaServer
@SpringBootApplication
public class SinoPayServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SinoPayServerApplication.class).web(true).run(args);
	}

}
