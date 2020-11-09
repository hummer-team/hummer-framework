package com.hummer.cache.plugin.driver;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.hummer.cache.plugin.KeyUtil;
import com.hummer.cache.plugin.SupplierEx;
import com.hummer.core.PropertiesContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SimpleGuavaCache implements InitializingBean, DisposableBean {

    private Cache<String, Object> cache;

    private void init() {
        if (disableCache()) {
            return;
        }

        cache = CacheBuilder.<String, Object>newBuilder()
                .expireAfterAccess(
                        PropertiesContainer.valueOf("guava.cache.timeout.millis", Long.class
                                , 30L), TimeUnit.SECONDS)
                .maximumSize(PropertiesContainer.valueOfInteger("guava.cache.data.max", 10000))
                .removalListener(new RemovalListener<String, Object>() {
                    /**
                     * Notifies the listener that a removal occurred at some point in the past.
                     *
                     * <p>This does not always signify that the key is now absent from the cache, as it may have
                     * already been re-added.
                     *
                     * @param notification notice
                     */
                    @Override
                    public void onRemoval(RemovalNotification<String, Object> notification) {
                        log.info("guava remove cache,k->{},v->{},cause->{}"
                                , notification.getKey()
                                , notification.getValue()
                                , notification.getCause());
                    }
                })
                .initialCapacity(PropertiesContainer.valueOfInteger("guava.cache.init.capacity", 16))
                .softValues()
                .recordStats()
                .build();
        log.info("guava cache init done.max size:{}"
                , PropertiesContainer.valueOfInteger("guava.cache.data.max", 10000));
    }

    private boolean disableCache() {
        if (!PropertiesContainer.valueOf("guava.cache.enable", Boolean.class, Boolean.FALSE)) {
            return true;
        }
        return false;
    }

    public String formatKey(final String nameSpace
            , final String businessCode
            , final Map<String, Object> parameterMap) {
        return KeyUtil.formatKey(nameSpace, businessCode, parameterMap);
    }

    public Object getOrSet(final String key
            , final SupplierEx noExistsAction) throws Throwable {
        if (disableCache()) {
            return noExistsAction.get();
        }
        Object o = cache.getIfPresent(key);
        log.debug("get data for guava cache,key is {},result is not null {}", key, o != null);
        if (o == null) {
            o = noExistsAction.get();
            log.debug("guava cache missed,this key is {},need execute noExistsAction,cache stats:{}"
                    , key
                    , cache.stats());
            if (o != null) {
                set(key, o);
            }
        }
        return o;
    }

    public void set(final String key, final Object o) {
        cache.put(key, o);
        log.debug("guava cache data item count:{}", cache.size());
    }

    public Object get(final String key) {

        return cache.getIfPresent(key);
    }

    public void clean() {
        long size = cache.size();
        cache.cleanUp();
        log.info("clean all cache item,count {}", size);
    }

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        clean();
    }
}
