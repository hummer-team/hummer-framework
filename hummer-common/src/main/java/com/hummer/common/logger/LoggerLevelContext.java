package com.hummer.common.logger;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LoggerLevelContext {
    private LoggerLevelContext() {

    }

    public static void changeLoggerLevel(final String level, final String packageOrClassName) {
        if (Strings.isNullOrEmpty(level) || Strings.isNullOrEmpty(packageOrClassName)) {
            throw new IllegalArgumentException("level and  packageOrClassName can't empty.");
        }

        getLoggerContext().forEach(logger -> {
            if (logger.getName().startsWith(packageOrClassName)) {
                logger.setLevel(Level.valueOf(level));
                logger.info("target package logger level set as {} level", level);
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
