package com.hummer.message.facade.publish;

import org.testng.annotations.Test;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:45
 **/
public class BasePublishTest {
    @Test
    public void sendMessage() {
        Publish.KAFKA_PUBLISH.publish(45, "test");
    }
}
