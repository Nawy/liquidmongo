package nl.exmg.liquidmongo.storage;

import com.mongodb.client.MongoClient;

public class DefaultMongoStorage implements StorageAdapter {

    private final MongoClient mongoClient;
    private final String databaseName;

    public DefaultMongoStorage(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public <ENTITY_T> StorageCollection<ENTITY_T> getCollection(String collectionName, Class<ENTITY_T> clazz) {
        return new DefaultMongoCollection<>(
                this.mongoClient,
                this.databaseName,
                collectionName,
                clazz
        );
    }
}
