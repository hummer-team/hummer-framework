package com.hummer.local.persistence.plugin.bean;

import com.hummer.local.persistence.plugin.LocalPersistence;

import java.util.List;
import java.util.Set;

public interface MapLocalPersistence extends LocalPersistence {
    void deletedAllForMap(String mapName);

    byte[] getForListWithOfIndex(String listName, int index);

    List<byte[]> getForListAll(String listName);

    List<byte[]> getForListWithOffset(String listName, long offset, long limit);

    List<byte[]> removeForListWithOffset(String listName, long offset, long limit);

    void addToList(String listName, byte[] item);

    void removeOfList(String listName);

    void addToQueue(String queueName, byte[] bytes);

    void addToSetAndListWithTraction(String listName, byte[] body);

    List<String> getAllOperationKey();

    Set<String> getSetAllKeys(String keyName);

    Set<String> getSetAllKeys();
}
