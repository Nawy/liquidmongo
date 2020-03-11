package nl.exmg.liquidmongo.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import java.util.Arrays;
import java.util.Collections;
import nl.exmg.liquidmongo.model.MigrationDoc;
import org.mongojack.JacksonMongoCollection;

import java.util.Objects;
import java.util.stream.StreamSupport;

public class DefaultChangelogCollection implements ChangelogCollection {

    private final MongoDatabase mongoDatabase;
    private final JacksonMongoCollection<MigrationDoc> collection;
    private final String collectionName;

    public DefaultChangelogCollection(MongoClient mongoClient, String databaseName, String collectionName) {
        Objects.requireNonNull(mongoClient);
        Objects.requireNonNull(databaseName);
        Objects.requireNonNull(collectionName);

        this.mongoDatabase = mongoClient.getDatabase(databaseName);
        this.collectionName = collectionName;
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        databaseName,
                        collectionName,
                        MigrationDoc.class
                );
    }

    public boolean isCollectionExists() {
        return StreamSupport.stream(
                this.mongoDatabase.listCollectionNames().spliterator(),
                false
        ).anyMatch(collectionName::equals);
    }

    public void createCollection() {
        mongoDatabase.createCollection(this.collectionName);
    }

    public void insert(MigrationDoc migrationDoc) {
        collection.insert(migrationDoc);
    }

    public MigrationDoc getLastStep() {
        return collection
            .aggregate(
                Collections.singletonList(
                    Accumulators.max("_id", "$id").getValue()
                ))
            .first();
    }
}
