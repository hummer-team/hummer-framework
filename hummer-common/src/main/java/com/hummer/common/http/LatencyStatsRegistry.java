package com.hummer.common.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.LatencyUtils.LatencyStats;

import java.util.List;
import java.util.Map;

public class LatencyStatsRegistry {
    private Map<String, LatencyStats> latencyStatsRegistryMap = Maps.newConcurrentMap();

    public List<String> getLatencyStatsRegistryNames() {
        return Lists.newArrayList(latencyStatsRegistryMap.keySet());
    }

    public LatencyStats getLatencyStatsInstance(String name) {
        if (! latencyStatsRegistryMap.containsKey(name)) {
            synchronized (LatencyStatsRegistry.class) {
                if (! latencyStatsRegistryMap.containsKey(name)) {
                    latencyStatsRegistryMap.put(name, new LatencyStats());
                }
            }
        }
        return latencyStatsRegistryMap.get(name);
    }
}
