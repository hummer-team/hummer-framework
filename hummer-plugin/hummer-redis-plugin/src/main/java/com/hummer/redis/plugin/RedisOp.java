package com.hummer.redis.plugin;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.hummer.redis.plugin.ops.BaseOp;
import com.hummer.redis.plugin.ops.HashSimpleOp;
import com.hummer.redis.plugin.ops.SetSimpleOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis operation instance factory
 *
 * @author bingy
 */
public class RedisOp implements InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisOp.class);

    private final Map<String, BaseOp> OPS_MAP = new ConcurrentHashMap<>();

    public Map<String, BaseOp> getAllOpInstance() {
        return ImmutableMap.copyOf(OPS_MAP);
    }

    public HashSimpleOp hash(String redisDbGroupName) {
        redisDbGroupName = format(redisDbGroupName, ":hash");
        HashSimpleOp ops = (HashSimpleOp) OPS_MAP.get(redisDbGroupName);
        if (ops != null) {
            return ops;
        }
        if ((ops = (HashSimpleOp) OPS_MAP.get(redisDbGroupName)) == null) {
            synchronized (OPS_MAP) {
                if ((ops = (HashSimpleOp) OPS_MAP.get(redisDbGroupName)) == null) {
                    String groupName = getGroupName(redisDbGroupName);
                    ops = new HashSimpleOp(groupName);
                    OPS_MAP.put(redisDbGroupName, ops);
                }
            }
        }
        return ops;
    }

    public HashSimpleOp hash() {
        return (HashSimpleOp) hash("simple:hash");
    }

    public SetSimpleOp set(String redisDbGroupName) {
        redisDbGroupName = format(redisDbGroupName, ":set");
        SetSimpleOp ops = (SetSimpleOp) OPS_MAP.get(redisDbGroupName);
        if (ops != null) {
            return ops;
        }
        if ((ops = (SetSimpleOp) OPS_MAP.get(redisDbGroupName)) == null) {
            synchronized (OPS_MAP) {
                if ((ops = (SetSimpleOp) OPS_MAP.get(redisDbGroupName)) == null) {
                    String groupName = getGroupName(redisDbGroupName);
                    ops = new SetSimpleOp(groupName);
                    OPS_MAP.put(redisDbGroupName, ops);
                }
            }
        }
        return ops;
    }

    private String format(String redisDbGroupName, String s) {
        if (!redisDbGroupName.endsWith(s)) {
            redisDbGroupName = String.format("%s%s", redisDbGroupName, s);
        }
        return redisDbGroupName;
    }

    public SetSimpleOp set() {
        return (SetSimpleOp) set("simple:set");
    }

    private String getGroupName(String redisDbGroupName) {
        Iterable<String> name = Splitter.on(":").omitEmptyStrings().split(redisDbGroupName);
        return Iterables.get(name, 0, redisDbGroupName);
    }

    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    public void destroy() throws Exception {
        for (Map.Entry<String, BaseOp> entry : OPS_MAP.entrySet()) {
            entry.getValue().closeAll();
        }
        LOGGER.info("`ben destroy` class all redis client done.");
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Map.Entry<String, BaseOp> entry : OPS_MAP.entrySet()) {
                entry.getValue().closeAll();
            }
            LOGGER.info("`thread addShutdownHook` class all redis client done.");
        }));
    }
}
