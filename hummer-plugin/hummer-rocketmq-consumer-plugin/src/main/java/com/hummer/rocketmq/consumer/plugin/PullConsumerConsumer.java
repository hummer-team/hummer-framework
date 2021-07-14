package com.hummer.rocketmq.consumer.plugin;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullConsumerConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullConsumerConsumer.class);

    private final DefaultLitePullConsumer pullConsumer;

    public PullConsumerConsumer(DefaultLitePullConsumer pullConsumer) {
        this.pullConsumer = pullConsumer;
        this.conn();
    }

    

    private void conn() {
        try {
            LOGGER.info("begin conn to name server {}", pullConsumer.getNamesrvAddr());
            long start = System.currentTimeMillis();
            this.pullConsumer.start();
            LOGGER.info("conn name server {} success,cost {} mills ", pullConsumer.getNamesrvAddr()
                    , System.currentTimeMillis() - start);
        } catch (Throwable e) {
            LOGGER.error("conn to name server {} failed,so interrupt consumer ", pullConsumer.getNamesrvAddr(), e);
            throw new RuntimeException(String.format("conn to name server %s failed,so interrupt consumer"
                    , pullConsumer.getNamesrvAddr()), e);
        }
    }

    private void stop() {
        this.pullConsumer.shutdown();
    }
}
