package com.hummer.local.persistence.plugin.bean;

import com.hummer.local.persistence.plugin.LocalPersistence;

public interface MapLocalPersistence extends LocalPersistence {
    void deletedAll(String mapName);
}
