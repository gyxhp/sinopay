package com.sinosoft.pay;

import com.sinosoft.pay.gateway.filter.AccessFilter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * @Description: 网关服务
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@EnableZuulProxy
@SpringCloudApplication
public class SinoPayGatewayApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SinoPayGatewayApplication.class).web(true).run(args);
	}

	@Bean
	public AccessFilter accessFilter() {
		return new AccessFilter();
	}

}
