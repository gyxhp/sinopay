package com.sinosoft.pay.service.mq;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

/**
 * @Description:
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@Configuration
public class MqConfig {

    public static final String PAY_NOTIFY_QUEUE_NAME = "sinopaytest.notify.queue";

    @Bean
    public Queue payNotifyQueue() {
        return new ActiveMQQueue(PAY_NOTIFY_QUEUE_NAME);
    }

}
