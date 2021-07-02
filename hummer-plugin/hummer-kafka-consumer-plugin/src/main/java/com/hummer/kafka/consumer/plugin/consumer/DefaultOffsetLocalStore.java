package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.common.utils.LongUtil;
import com.hummer.local.persistence.plugin.LocalPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static com.hummer.kafka.consumer.plugin.KafkaConsumerConstant.OFFSET_STORE_DEFAULT;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/26 16:58
 **/
@Service(value = OFFSET_STORE_DEFAULT)
public class DefaultOffsetLocalStore implements OffsetStore {
    @Autowired
    private LocalPersistence persistence;

    /**
     * persistence kafka consumer offset
     *
     * @param topic  topic
     * @param offset offset
     * @return void
     * @author liguo
     * @date 2019/8/26 16:59
     * @since 1.0.0
     **/
    @Override
    public void store(final @NotNull String topic, final long offset, final int offsetKey) {
        persistence.put(topic
                , String.valueOf(offsetKey)
                , LongUtil.convertToBytes(offset));
    }

    /**
     * get offset value
     *
     * @param topic
     * @return long
     * @author liguo
     * @date 2019/8/26 17:02
     * @since 1.0.0
     **/
    @Override
    public long getOffset(final @NotNull String topic, final int offsetKey) {
        return LongUtil.convertToLong(persistence.get(topic, String.valueOf(offsetKey)));
    }
}
