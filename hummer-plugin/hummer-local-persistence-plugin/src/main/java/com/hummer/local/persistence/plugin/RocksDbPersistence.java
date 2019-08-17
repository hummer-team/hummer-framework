package com.hummer.local.persistence.plugin;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
                db.delete(getByKey(key));
            }
        } catch (RocksDBException e) {
            LOGGER.error("rocksDb delete data failed,key is {}", key, e);
        }
    }

    @Override
    public byte[] get(final String keyNameSpace, final String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyNameSpace), "column family can not null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");

        return operationColumnFamily(keyNameSpace, db -> {
            try {
                return db.get(getByKey(key));
            } catch (RocksDBException e) {
                throw new SysException(50000
                        , String.format("rocks db put error,column family is %s key is %s"
                        , keyNameSpace
                        , key)
                        , e);
            }
        });
    }

    @Override
    public void put(final String keyNameSpace, final String key, final byte[] value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyNameSpace), "column family can not null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");

        operationColumnFamily(keyNameSpace, db -> {
            try {
                db.put(getByKey(key), value);
                return null;
            } catch (RocksDBException e) {
                throw new SysException(50000
                        , String.format("rocks db put error,column family is %s key is %s"
                        , keyNameSpace
                        , key)
                        ,e);
            }
        });
    }

    @Override
    public void put(final String key, final byte[] value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key not null");
        try (final Options options = new Options()) {
            options.setCreateIfMissing(true);
            try (final RocksDB db = RocksDB.open(options, getDbPath())) {
                db.put(getBytes(key), value);
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
                return db.get(getBytes(key));
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


    private byte[] getBytes(final String key) {
        try {
            return key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("rocks db serial key failed,key is {},exception {}", key, e);
            throw new SysException(50000, "rocks db serial key failed,reason is charset utf-8 not support");
        }
    }

    private <T> T operationColumnFamily(final String keyNameSpace, final Function<RocksDB, T> dbConsumer) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyNameSpace), "column family can not null");
        Preconditions.checkArgument(dbConsumer != null, "consumer not null");

        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(getByKey(keyNameSpace), cfOpts)
            );

            final List<ColumnFamilyHandle> columnFamilyHandleList =
                    new ArrayList<>();

            try (final DBOptions options = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);
                 final RocksDB db = RocksDB.open(options
                         , getDbPath()
                         , cfDescriptors
                         , columnFamilyHandleList)) {
                try {
                    return dbConsumer.apply(db);
                } finally {
                    for (final ColumnFamilyHandle columnFamilyHandle :
                            columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                }
            }
        } catch (RocksDBException | SysException e) {
            LOGGER.error("rocks db operation with column family failed, column family key is {}"
                    , keyNameSpace
                    , e);
        }

        return null;
    }
}
