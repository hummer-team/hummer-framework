package com.hummer.local.persistence.plugin;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.collections.CollectionUtils;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * use rocks db store object
 *
 * @author bingy
 * @link https://github.com/facebook/rocksdb/wiki/RocksJava-Basics
 */
@Service
public class RocksDbPersistence implements RocksDBLocalPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocksDbPersistence.class);

    @PostConstruct
    private void init() {
        RocksDB.loadLibrary();
    }


    @Override
    public void delete(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");
        try (final Options options = new Options()) {
            options.setCreateIfMissing(true);
            try (final RocksDB db = RocksDB.open(options, getDbPath())) {
                db.delete(convertKeyToBytes(key));
            }
        } catch (RocksDBException e) {
            LOGGER.error("rocksDb delete data failed,key is {}", key, e);
        }
    }

    @Override
    public byte[] get(final String columnFamilyName, final String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(columnFamilyName), "column family can not null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");

        return operationColumnFamily(columnFamilyName, (db, cf) -> {
            try {
                return db.get(convertKeyToBytes(key));
            } catch (RocksDBException e) {
                throw new SysException(50000
                        , String.format("rocks db put error,column family is %s key is %s"
                        , columnFamilyName
                        , key)
                        , e);
            }
        },"GET");
    }

    @Override
    public void put(final String columnFamilyName, final String key, final byte[] value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(columnFamilyName), "column family can not null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");

        operationColumnFamily(columnFamilyName, (db, cf) -> {
            try {
                db.put(convertKeyToBytes(key), value);
                return null;
            } catch (RocksDBException e) {
                throw new SysException(50000
                        , String.format("rocks db put error,column family is %s key is %s"
                        , columnFamilyName
                        , key)
                        , e);
            }
        },"PUT");
    }

    @Override
    public void put(final String key, final byte[] value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");
        try (final Options options = new Options()) {
            options.setCreateIfMissing(true);
            try (final RocksDB db = RocksDB.open(options, getDbPath())) {
                db.put(convertKeyToBytes(key), value);
            }
        } catch (RocksDBException e) {
            LOGGER.error("rocks db store failed,key value {} ", key, e);
        }
    }

    @Override
    public byte[] getByKey(final String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");
        try (final Options options = new Options()) {
            options.setCreateIfMissing(true);
            try (final RocksDB db = RocksDB.open(getDbPath())) {
                return db.get(convertKeyToBytes(key));
            }
        } catch (RocksDBException e) {
            LOGGER.error("rocks db get failed,key value {} ", key, e);
        }
        return new byte[0];
    }


    private String getDbPath() {
        return PropertiesContainer.valueOfString("hummer.local.persistence.path"
                , "/home/hummer/db");
    }


    private byte[] convertKeyToBytes(final String key) {
        try {
            return key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("rocks db serial key failed,key is {},exception {}", key, e);
            throw new SysException(50000, "rocks db serial key failed,reason is charset utf-8 not support");
        }
    }

    private <T> T operationColumnFamily(final String keyColumnFamilyName
            , final BiFunction<RocksDB, ColumnFamilyHandle, T> dbConsumer
            , final String operationType) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyColumnFamilyName)
                , "column family can not null");
        Preconditions.checkArgument(dbConsumer != null
                , "consumer not null");
        final long start = System.currentTimeMillis();
        //wiki:https://github.com/facebook/rocksdb/wiki/RocksJava-Basics#opening-a-database-with-column-families
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Lists.newArrayList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(convertKeyToBytes(keyColumnFamilyName), cfOpts)
            );
            Options options = new Options();
            options.setCreateIfMissing(true);
            List<byte[]> cfBytes = RocksDB.listColumnFamilies(options, getDbPath());
            //add already exist column family
            if (CollectionUtils.isNotEmpty(cfBytes)) {
                for (byte[] by : cfBytes) {
                    cfDescriptors.add(new ColumnFamilyDescriptor(by, new ColumnFamilyOptions()));
                }
            }
            final List<ColumnFamilyHandle> columnFamilyHandleList =
                    new ArrayList<>();

            try (final DBOptions dbOptions = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);

                 final RocksDB db = RocksDB.open(dbOptions
                         , getDbPath()
                         , cfDescriptors
                         , columnFamilyHandleList)) {

                try {
                    T result = dbConsumer.apply(db, null);
                    LOGGER.info("rocks db operation column Family {} {} cost {} ms"
                            , keyColumnFamilyName
                            , operationType
                            , System.currentTimeMillis() - start);
                    return result;
                } finally {
                    for (final ColumnFamilyHandle cfHandle :
                            columnFamilyHandleList) {
                        cfHandle.close();
                    }
                }
            }
        } catch (RocksDBException | SysException e) {
            LOGGER.error("rocks db operation with column family failed, column family key is {}"
                    , keyColumnFamilyName
                    , e);
        }

        return null;
    }
}
