package com.hummer.config.bo;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

/**
 * ConfigListenerKey
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/28 14:39
 */
@Builder
@Getter
public class ConfigListenerKey {

    @NotEmpty
    private String dataId;

    @NotEmpty
    private String groupId;

    /**
     * 订阅的配置属性key
     */
    @NotEmpty
    private List<String> propertiesKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigListenerKey key = (ConfigListenerKey) o;
        return Objects.equals(dataId, key.dataId) &&
                Objects.equals(groupId, key.groupId) &&
                ((propertiesKey == null && key.getPropertiesKey() == null)
                        || (propertiesKey != null && propertiesKey.containsAll(key.getPropertiesKey())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, groupId, propertiesKey);
    }

    @Override
    public String toString() {
        return String.format("ConfigListenerKey=[dataId:%s,groupId:%s,propertiesKey:%s]"
                , dataId, groupId, propertiesKey);
    }
}
