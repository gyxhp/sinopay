package com.sinosoft.pay.mgr.Filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author yang@dehong
 * 2018-07-15 15:20
 */


@Configuration
public class ApiGatewayConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SystemInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

}

