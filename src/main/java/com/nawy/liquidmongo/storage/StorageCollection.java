package com.nawy.liquidmongo.storage;

import java.util.List;
import java.util.stream.Stream;

public interface StorageCollection <ENTITY_T> {

    int bulkWrite(List<ENTITY_T> entities);

    Stream<ENTITY_T> findAll(int skipAmount, int pageSize);

    void createCollection(String collectionName);
    void renameCollection(String collectionName);

    void drop();
}
