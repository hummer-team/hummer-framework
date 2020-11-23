package com.hummer.data.sync.plugin.pipeline;

import com.alibaba.fastjson.JSONObject;
import com.hummer.common.utils.CommonUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.data.sync.plugin.enums.OrderSyncEnums;
import com.hummer.data.sync.plugin.model.OrderSyncMessage;
import com.hummer.data.sync.plugin.util.Util;
import com.panli.spaceship.mq.producer.client.MQSendCallBack;
import com.panli.spaceship.mq.producer.client.MQSendUtil;
import com.panli.spaceship.mq.producer.client.model.MQMessage;
import com.panli.spaceship.mq.producer.client.model.MQMessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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
        if (data == null) {
            log.debug("order data  sync sending is null");
            return false;
        }
        return MQSendUtil.send(composeMQMessage(data));
    }

    public void asyncPush(OrderSyncMessage data, Function<Object, Boolean> function) {
        if (data == null) {
            log.debug("order data  sync sending is null");
        }
        MQSendUtil.asyncSend(composeMQMessage(data), new MQSendCallBack() {
            @Override
            public void complete(MQMessageExt message) {
                function.apply(true);
            }

            @Override
            public void fail(MQMessageExt message, Exception e) {
                function.apply(false);
            }
        });
    }

    private MQMessage composeMQMessage(OrderSyncMessage data) {
        MQMessage message = new MQMessage();
        message.setBusinessCode(CommonUtil.ifNullDefault(data.getTopic(), OrderSyncEnums.MqTopics.TRANSACTION.getValue()));
        data.setTopic(message.getBusinessCode());
        message.setOperationCode(Util.composeTopicTag(PropertiesContainer.valueOfStringWithAssertNotNull("spring.application.name")
                , data.getBusinessType(), data.getAction()));
        message.setBodys(JSONObject.toJSONBytes(data));
        message.setSendMQTimeout(3000);
        message.setBusinessId(data.getBusinessId());
        return message;
    }
}
