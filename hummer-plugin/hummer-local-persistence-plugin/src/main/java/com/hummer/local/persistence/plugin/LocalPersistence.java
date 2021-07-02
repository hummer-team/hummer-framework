package com.hummer.local.persistence.plugin;

/**
 * @author bingy
 */
public interface LocalPersistence {
    /**
     * deleted data by key
     *
     * @param key key
     */
    void delete(final String key);

    /**
     * store
     *
     * @param key   store for key
     * @param value
     */
    void put(final String key, final byte[] value);

    /**
     * get value,if exception then return null
     *
     * @param key key
     * @return
     */
    byte[] getByKey(final String key);

    /**
     * with column family key
     *
     * @param columnFamilyName column family name
     * @param key              key
     * @param value            value
     */
    void put(final String columnFamilyName, final String key, final byte[] value);

    /**
     * get key name space in data
     *
     * @param columnFamilyName column family name
     * @param key              key
     * @return
     */
    byte[] get(final String columnFamilyName, final String key);

    /**
     * if return true then enable local store,else disable
     *
     * @return
     */
    boolean enable();
}
