package com.hummer.data.sync.plugin.util;

/**
 * Util
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/11/17 11:34
 */
public class Util {

    public static String composeTopicTag(String applicationName, String businessType, String actionType) {

        return applicationName + "-" + businessType + "-" + actionType;
    }

}
