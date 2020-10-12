package com.hummer.config.enums;

/**
 * ConfigEnums
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:42
 */
public class ConfigEnums {

    public enum ConfigActions {

        ADD, UPDATE, DELETE,
        ;

    }

    public enum ConfigType {
        JSON("json"), PROPERTIES("properties");
        private String value;

        ConfigType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
