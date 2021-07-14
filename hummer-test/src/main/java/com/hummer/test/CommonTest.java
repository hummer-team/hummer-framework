package com.hummer.test;

import com.google.common.base.Splitter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CommonTest {
    @Test
    public void url() {
        Assert.assertEquals("2021-06-24+11%3A46%3A57", URLEncoder.encode("2021-06-24 11:46:57"));
        URI uri = URI.create("http://local.gjpqqd.com:5929/Service/ERPService.asmx/EMallApi?method=emall.token.get&timestamp=2021-06-24+11%3A46%3A57&format=json&app_key=000210611170422897&v=1.0&sign=&sign_method=md5");
        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(uri.getQuery());
        Assert.assertEquals("emall.token.get"
                , map.get("method"));
    }

    @Test
    public void sendRocketMqDirect() throws MQClientException, UnsupportedEncodingException, RemotingException
            , InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("test");
        producer.setNamesrvAddr("10.28.28.20:9876");
        //producer.setSendMsgTimeout(10000);
        producer.start();
        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("test003" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%s%n", sendResult);
                    countDownLatch.countDown();
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%s%n", e);
                }
            });

        }
        //Shut down once the producer instance is not longer in use.
        countDownLatch.await(10, TimeUnit.SECONDS);
        producer.shutdown();
    }
}
