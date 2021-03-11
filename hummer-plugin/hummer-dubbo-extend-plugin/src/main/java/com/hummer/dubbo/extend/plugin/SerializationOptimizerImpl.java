package com.hummer.dubbo.extend.plugin;

import com.google.common.collect.Lists;
import com.hummer.common.utils.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.serialize.support.SerializationOptimizer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * dubbo serialization collection
 *
 * @author edz
 */
@Slf4j
public class SerializationOptimizerImpl implements SerializationOptimizer {
    private static final List<Class<?>> LIST = Lists.newArrayListWithCapacity(16);
    private static String packageName;

    /**
     * Get serializable classes
     *
     * @return serializable classes
     */
    @Override
    public Collection<Class<?>> getSerializableClasses() {
        log.debug("begin register {} dto count {} to cache ", packageName, LIST.size());
        return LIST;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        SerializationOptimizerImpl.packageName = packageName;
    }

    public void registerDto() {
        Set<Class<?>> cls = ResourceUtil.getClassesByPackageName(packageName);
        LIST.addAll(cls);
    }
}
