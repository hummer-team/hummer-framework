package com.hummer.config.logger;

import com.hummer.common.logger.LoggerLevelContext;
import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.bo.ConfigPropertiesChangeInfoBo;
import com.hummer.config.listener.AbstractConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * logger level change listener
 *
 * @author lee
 */
@Component("loggerConfigListener")
@Slf4j
public class LoggerConfigListener extends AbstractConfigListener {
    @Override
    public void handleChange(ConfigListenerKey key, List<ConfigPropertiesChangeInfoBo> changeInfoBos) {
        for (ConfigPropertiesChangeInfoBo changeInfoBo : changeInfoBos) {
            LoggerLevelContext.changeLoggerLevel((String) changeInfoBo.getCurrentValue()
                    , changeInfoBo.getPropertiesKey());
        }
        log.info("logger level change listener handle done,key: {} config: {}",key,changeInfoBos);
    }
}
