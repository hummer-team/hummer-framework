package com.hummer.message.facade.bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 18:37
 **/
@Configuration
@ComponentScan(value = {"com.hummer.message"})
public class MessageBusBean {
}
