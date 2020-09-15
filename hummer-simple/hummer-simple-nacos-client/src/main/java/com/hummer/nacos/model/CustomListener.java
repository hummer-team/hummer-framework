package com.hummer.nacos.model;

import com.alibaba.fastjson.JSON;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.listener.AbstractConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CustomListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/31 18:41
 */
public class CustomListener extends AbstractConfigListener {


    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListener.class);

    @Override
    public void handleChange(ConfigListenerKey key, List<ConfigPropertiesChangeInfoBo> changeInfoBos) {
        LOGGER.info("config has changed == key == {},changes=={}", key, JSON.toJSONString(changeInfoBos));
    }
}
