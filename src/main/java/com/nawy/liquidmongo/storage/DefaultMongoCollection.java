package com.nawy.liquidmongo.storage;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.InsertOneModel;
import org.mongojack.JacksonMongoCollection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultMongoCollection <ENTITY_T> implements StorageCollection <ENTITY_T> {

    private final JacksonMongoCollection<ENTITY_T> collection;
    private final String databaseName;

    public DefaultMongoCollection(
            MongoClient mongoClient,
            String databaseName,
            String collectionName,
            Class<ENTITY_T> clazz
    ) {
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        databaseName,
                        collectionName,
                        clazz
                );
        this.databaseName = databaseName;
    }

    @Override
    public int bulkWrite(List<ENTITY_T> entities) {
        return this.collection.bulkWrite(
                entities.stream()
                        .map(InsertOneModel::new)
                        .collect(Collectors.toList())
        ).getInsertedCount();
    }

    public Stream<ENTITY_T> findAll(int skipAmount, int pageSize) {
        return StreamSupport
                .stream(
                        collection.find().skip(skipAmount).limit(pageSize).spliterator(),
                        false
                );
    }

    public void drop() {
        this.collection.drop();
    }

    @Override
    public void renameCollection(String collectionName) {
        this.collection.renameCollection(new MongoNamespace(databaseName, "collectionName"));
    }
}
