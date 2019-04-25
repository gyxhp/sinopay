package com.sinosoft.pay.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SinoPayShopApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SinoPayShopApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners();
        return application.sources(applicationClass);
    }

    private static Class<SinoPayShopApplication> applicationClass = SinoPayShopApplication.class;

}