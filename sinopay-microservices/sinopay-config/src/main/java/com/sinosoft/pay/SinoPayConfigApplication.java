package com.sinosoft.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @Description: sinopay配置中心服务端
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class SinoPayConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(SinoPayConfigApplication.class, args);
    }
}
