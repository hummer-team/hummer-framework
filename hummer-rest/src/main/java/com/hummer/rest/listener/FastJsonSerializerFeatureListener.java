package com.hummer.rest.listener;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.listener.AbstractConfigListener;
import com.hummer.rest.bean.CustomFastJsonConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * FastJsonSerializerFeatrueListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/2/4 15:41
 */
public class FastJsonSerializerFeatureListener extends AbstractConfigListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonSerializerFeatureListener.class);

    private FastJsonConfig fastJsonConfig;

    public FastJsonSerializerFeatureListener(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    @Override
    public void handleChange(ConfigListenerKey key, List<ConfigPropertiesChangeInfoBo> changeInfoBos) {
        if (CollectionUtils.isEmpty(changeInfoBos)) {
            return;
        }
        ConfigPropertiesChangeInfoBo changeInfoBo = changeInfoBos.get(0);
        LOGGER.info("fast json SerializerFeature config change,original=={},current=={}"
                , changeInfoBo.getOriginValue(), changeInfoBo.getCurrentValue());

        fastJsonConfig.setSerializerFeatures(CustomFastJsonConfigs
                .getSerializerFeature().toArray(new SerializerFeature[0]));
    }
}
