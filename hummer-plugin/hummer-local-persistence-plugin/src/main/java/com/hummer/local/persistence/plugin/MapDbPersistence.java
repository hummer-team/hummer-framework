package com.hummer.local.persistence.plugin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hummer.core.PropertiesContainer;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.IndexTreeList;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lee
 */
@Service
public class MapDbPersistence implements MapLocalPersistence {
    private static final String DEFAULT_MAP_NAME = "default_map";
    private static final String DEFAULT_LIST_NAME = "default_list";
    private static final String DEFAULT_KEY_SET_NAME = "default_keySet";

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

    @Override
    public byte[] getForListWithOfIndex(String listName, int index) {
        return listActionOfReturn(listName, list -> list.get(index));
    }

    @Override
    public List<byte[]> getForListAll(String listName) {
        return listActionOfReturn(listName, list -> Lists.newArrayList(list));
    }

    @Override
    public List<byte[]> getForListWithOffset(String listName, long offset, long limit) {
        return listActionOfReturn(listName, list -> {
            return ((List<byte[]>) list).stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<byte[]> removeForListWithOffset(String listName, long offset, long limit) {
        return listActionOfReturn(listName, list -> {
            List<byte[]> list1 = Lists.newArrayListWithCapacity((int) limit);
            for (long i = 0; i < limit && i < list.size(); i++) {
                list1.add(list.removeAt((int) i));
            }
            return list1;
        },true);
    }

    @Override
    public void addToList(String listName, byte[] item) {
        listAction(listName, list -> list.add(item));
    }

    @Override
    public void addToQueue(String queueName, byte[] bytes) {

    }

    @Override
    public void removeOfList(String listName) {
        listAction(listName, list -> list.clear());
    }

    @Override
    public void addToSetAndListWithTraction(String listName, byte[] body) {
        dbAction(db -> {
            try {
                HTreeMap.KeySet<String> keySet = db
                        .hashSet(DEFAULT_KEY_SET_NAME, Serializer.STRING)
                        .createOrOpen();
                keySet.add(listName);
                IndexTreeList<byte[]> list = db
                        .indexTreeList(listName, Serializer.BYTE_ARRAY)
                        .createOrOpen();
                list.add(body);
                db.commit();
            } catch (Exception e) {
                db.rollback();
            }
        });
    }

    @Override
    public Set<String> getSetAllKeys() {
        return getSetAllKeys(null);
    }

    @Override
    public Set<String> getSetAllKeys(String keyName) {
        Set<String> list = Sets.newHashSetWithExpectedSize(16);
        dbAction(db -> {
            String name = Strings.isNullOrEmpty(keyName) ? DEFAULT_KEY_SET_NAME : keyName;
            HTreeMap.KeySet<String> keySet = db
                    .hashSet(name, Serializer.STRING)
                    .createOrOpen();
            list.addAll(Lists.newArrayList(keySet.iterator()));
        });

        return list;
    }

    @Override
    public List<String> getAllOperationKey() {
        return dbActionOfReturn(db -> Lists.newArrayList(db.getAllNames()));
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

    private void listAction(String listName, Consumer<IndexTreeList<byte[]>> listConsumer) {
        try (DB db = db()) {
            IndexTreeList<byte[]> map = db
                    .indexTreeList(listName, Serializer.BYTE_ARRAY)
                    .createOrOpen();

            listConsumer.accept(map);
        }
    }

    private <T> T listActionOfReturn(String listName, Function<IndexTreeList<byte[]>, T> listConsumer){
        return listActionOfReturn(listName,listConsumer,false);
    }

    private <T> T listActionOfReturn(String listName, Function<IndexTreeList<byte[]>, T> listConsumer,boolean commit) {
        try (DB db = db()) {
            IndexTreeList<byte[]> list = db
                    .indexTreeList(listName, Serializer.BYTE_ARRAY)
                    .createOrOpen();
            T result = listConsumer.apply(list);
            db.commit();
            return result;
        }
    }

    private void mapAction(String mapName, Consumer<ConcurrentMap<String, byte[]>> mapConsumer) {
        try (DB db = db()) {
            ConcurrentMap<String, byte[]> map = db
                    .hashMap(mapName, Serializer.STRING, Serializer.BYTE_ARRAY)
                    .createOrOpen();
            mapConsumer.accept(map);
        }
    }

    private <T> T mapActionOfReturn(String mapName, Function<ConcurrentMap<String, byte[]>, T> mapConsumer) {
        try (DB db = db()) {
            ConcurrentMap<String, byte[]> map = db
                    .hashMap(mapName, Serializer.STRING, Serializer.BYTE_ARRAY)
                    .createOrOpen();

            return mapConsumer.apply(map);
        }
    }

    private void dbAction(Consumer<DB> dbConsumer) {
        try (DB db = db()) {
            dbConsumer.accept(db);
        }
    }

    private <T> T dbActionOfReturn(Function<DB, T> dbConsumer) {
        try (DB db = db()) {
            return dbConsumer.apply(db);
        }
    }

    @NotNull
    private DB db() {
        return DBMaker.fileDB(dbPath()).fileMmapEnable().transactionEnable().closeOnJvmShutdown().make();
    }

    @Override
    public void deletedAllForMap(String mapName) {
        if (StringUtils.isEmpty(mapName)) {
            mapName = DEFAULT_MAP_NAME;
        }
        mapAction(mapName, map -> map.clear());
    }
}
