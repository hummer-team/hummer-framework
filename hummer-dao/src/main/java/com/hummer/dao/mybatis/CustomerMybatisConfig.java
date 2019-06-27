package com.hummer.dao.mybatis;

import org.apache.ibatis.session.Configuration;

/**
 * impl this interface register customer configuration.
 *
 * @author bingy
 * @since 1.0.0
 */
public interface CustomerMybatisConfig {
    /**
     * settings configuration
     *
     * @param configuration mybatis session configuration
     */
    void settings(Configuration configuration);
}
