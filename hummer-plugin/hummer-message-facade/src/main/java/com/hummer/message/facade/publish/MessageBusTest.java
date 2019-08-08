package com.hummer.message.facade.publish;

import com.hummer.message.facade.metadata.Message;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import org.testng.annotations.Test;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:45
 **/
public class MessageBusTest {
    @Test
    public void sendMessage() {
        MessageBus.publish(Message
                .builder()
                .messageDriver(MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME)
                .appId("test")
                .build());
    }
}
