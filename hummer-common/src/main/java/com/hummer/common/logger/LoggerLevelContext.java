package com.hummer.common.logger;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LoggerLevelContext {
    private final static String LOGGER_ROOT = "logger.root";

    private LoggerLevelContext() {

    }

    public static void changeLoggerLevel(final String level, final String packageOrClassName) {
        if (Strings.isNullOrEmpty(level) || Strings.isNullOrEmpty(packageOrClassName)) {
            throw new IllegalArgumentException("level and  packageOrClassName can't empty.");
        }


        if (LOGGER_ROOT.equalsIgnoreCase(packageOrClassName)) {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            //Configurator.setRootLevel(Level.valueOf(level));
            LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            loggerConfig.setLevel(Level.valueOf(level));
            ctx.updateLoggers();
        }

        getLoggerContext().forEach(logger -> {
            if (StringUtils.startsWithIgnoreCase(logger.getName(), packageOrClassName)) {
                logger.setLevel(Level.valueOf(level));
                logger.debug("set target package logger level: {} - {} - {}"
                        , logger.getName()
                        , packageOrClassName
                        , level);
                //logger.getContext().updateLoggers();
            }
        });
    }

    private static List<Logger> getLoggerContext() {
        Collection<Logger> current = LoggerContext.getContext(false).getLoggers();
        Collection<Logger> notCurrent = LoggerContext.getContext().getLoggers();
        List<Logger> all = Lists.newArrayList(current);
        all.addAll(notCurrent);
        return all;
    }

    public static List<String> getAllLoggerLevel() {
        return getLoggerContext()
                .stream()
                .map(m -> String.format("%s -> %s", m.getName(), m.getLevel().toString()))
                .collect(Collectors.toList());
    }
}
