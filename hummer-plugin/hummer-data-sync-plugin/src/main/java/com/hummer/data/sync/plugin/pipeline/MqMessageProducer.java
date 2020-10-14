package com.hummer.data.sync.plugin.pipeline;

import com.alibaba.fastjson.JSONObject;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.data.sync.plugin.model.OrderSyncMessage;
import com.panli.spaceship.mq.producer.client.MQSendUtil;
import com.panli.spaceship.mq.producer.client.model.MQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 11:13
 */
@Component
@Slf4j
public class MqMessageProducer {

    public Boolean push(OrderSyncMessage data) {
        AppBusinessAssert.isTrue(data != null, 500, "order sync data is null");
        return MQSendUtil.send(composeMQMessage(data));
    }

    private MQMessage composeMQMessage(OrderSyncMessage data) {
        MQMessage message = new MQMessage();
        message.setBusinessCode(data.getBusinessType());
        message.setOperationCode(data.getAction());
        message.setBodys(JSONObject.toJSONBytes(data));
        message.setSendMQTimeout(3000);
        return message;
    }
}
