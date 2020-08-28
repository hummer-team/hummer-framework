package com.hummer.config.listener;

import java.util.Map;

/**
 * ConfigListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:34
 */
public interface ConfigListener {

    void handleChange(final Map<String, String> configInfo);
}
