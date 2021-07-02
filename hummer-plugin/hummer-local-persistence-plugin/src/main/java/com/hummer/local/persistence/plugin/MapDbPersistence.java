package com.hummer.local.persistence.plugin;

import com.hummer.core.PropertiesContainer;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author lee
 */
@Service
public class MapDbPersistence implements MapLocalPersistence {
    private static final String DEFAULT_MAP_NAME = "default";

    /**
     * deleted data by key
     *
     * @param key key
     */
    @Override
    public void delete(String key) {
        mapAction(map -> map.remove(key));
    }

    /**
     * store
     *
     * @param key   store for key
     * @param value
     */
    @Override
    public void put(String key, byte[] value) {
        mapAction(map -> map.put(key, value));
    }

    /**
     * get value,if exception then return null
     *
     * @param key key
     * @return
     */
    @Override
    public byte[] getByKey(String key) {
        final byte[][] val = {new byte[0]};
        mapAction(map -> val[0] = map.get(key));
        return val[0];
    }

    /**
     * with column family key
     *
     * @param columnFamilyName column family name
     * @param key              key
     * @param value            value
     */
    @Override
    public void put(String columnFamilyName, String key, byte[] value) {
        mapAction(columnFamilyName, map -> map.put(key, value));
    }

    /**
     * get key name space in data
     *
     * @param columnFamilyName column family name
     * @param key              key
     * @return
     */
    @Override
    public byte[] get(String columnFamilyName, String key) {
        final byte[][] val = {new byte[0]};
        mapAction(columnFamilyName, map -> val[0] = map.get(key));
        return val[0];
    }

    /**
     * if return true then enable local store,else disable
     *
     * @return
     */
    @Override
    public boolean enable() {
        return PropertiesContainer.valueOf("hummer.mapDb.enable", Boolean.class, false);
    }

    private String dbPath() {
        String dir = PropertiesContainer.valueOfString("hummer.mapDb.path", System.getProperty("user.dir"));
        return String.format("%s/hummer.db", dir);
    }

    private void mapAction(Consumer<ConcurrentMap<String, byte[]>> mapConsumer) {
        mapAction(DEFAULT_MAP_NAME, mapConsumer);
    }

    private void mapAction(String mapName, Consumer<ConcurrentMap<String, byte[]>> mapConsumer) {
        try (DB db = DBMaker.fileDB(dbPath()).fileMmapEnable().make()) {
            ConcurrentMap<String, byte[]> map = db
                    .hashMap(mapName, Serializer.STRING, Serializer.BYTE_ARRAY)
                    .createOrOpen();

            mapConsumer.accept(map);
        }
    }

    @Override
    public void deletedAll(String mapName) {
        if (StringUtils.isEmpty(mapName)) {
            mapName = DEFAULT_MAP_NAME;
        }
        mapAction(mapName, map -> map.clear());
    }
}
