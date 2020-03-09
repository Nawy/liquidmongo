package nl.exmg.liquidmongo.migration;

import nl.exmg.liquidmongo.storage.StorageAdapter;
import nl.exmg.liquidmongo.storage.StorageCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MigrationStep <OLD_T, NEW_T> {

    // logger
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private int migrationPageSize = 100;
    private String temporaryTablePrefix = UUID.randomUUID().toString() + "_";
    private String changeLogPrefix = "";

    private int order;
    private String uniqueName;
    protected String newCollectionName;
    protected String oldCollectionName;
    private String collectionName;
    private Boolean removeOld = true;
    private Class<OLD_T> oldClass;
    private Class<NEW_T> newClass;

    public MigrationStep(
            int order,
            String oldCollectionName,
            String newCollectionName,
            Class<OLD_T> oldClass,
            Class<NEW_T> newClass
    ) {
        Objects.requireNonNull(oldCollectionName);
        Objects.requireNonNull(newCollectionName);
        Objects.requireNonNull(oldClass);
        Objects.requireNonNull(newClass);

        this.order = order;
        this.uniqueName = changeLogPrefix + oldCollectionName + "_" + newCollectionName;
        this.collectionName = oldCollectionName;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }

    public MigrationStep(
            int order,
            String collectionName,
            Class<OLD_T> oldClass,
            Class<NEW_T> newClass
    ) {
        Objects.requireNonNull(collectionName);
        Objects.requireNonNull(oldClass);
        Objects.requireNonNull(newClass);

        this.order = order;
        this.uniqueName = changeLogPrefix + collectionName;
        this.collectionName = collectionName;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }

    public void migrate(StorageAdapter storage) {

        if (newCollectionName == null) {
            final String temporaryName = collectionName + "_" + temporaryTablePrefix;
            StorageCollection<OLD_T> currentCollection = storage.getCollection(collectionName, oldClass);
            StorageCollection<NEW_T> newCollection = storage.getCollection(temporaryName, newClass);

            newCollection.createCollection(temporaryName);

            this.copyTo(currentCollection, newCollection, this::migrateEntity);
            currentCollection.drop();

            newCollection.renameCollection(collectionName);

            log.info(collectionName + " migrated via [" + this.getClass().getName() + "]");
        } else {
            StorageCollection<OLD_T> currentCollection = storage.getCollection(oldCollectionName, oldClass);
            StorageCollection<NEW_T> newCollection = storage.getCollection(newCollectionName, newClass);

            newCollection.createCollection(newCollectionName);

            this.copyTo(currentCollection, newCollection, this::migrateEntity);
            if (removeOld) {
                currentCollection.drop();
            }
            log.info(oldCollectionName + " migrated to " + newCollectionName + " via [" + this.getClass().getName() + "]");
        }
    }

    public void rollback(StorageAdapter storage) {

        if (newCollectionName == null) {
            final String temporaryName = collectionName + "_" + temporaryTablePrefix;
            StorageCollection<NEW_T> currentCollection = storage.getCollection(collectionName, newClass);
            StorageCollection<OLD_T> previousCollection = storage.getCollection(temporaryName, oldClass);
            previousCollection.createCollection(temporaryName);

            this.copyTo(currentCollection, previousCollection, this::rollbackEntity);

            currentCollection.drop();
            previousCollection.renameCollection(collectionName);
            log.info(collectionName + " got back via [" + this.getClass().getName() + "]");
        } else {
            StorageCollection<NEW_T> currentCollection = storage.getCollection(newCollectionName, newClass);
            StorageCollection<OLD_T> previousCollection = storage.getCollection(oldCollectionName, oldClass);

            previousCollection.drop();
            previousCollection.createCollection(oldCollectionName);
            this.copyTo(currentCollection, previousCollection, this::rollbackEntity);
            currentCollection.drop();
            log.info(newCollectionName + " got back to " + oldCollectionName + " via [" + this.getClass().getName() + "]");
        }
    }

    private <FROM_T, TO_T> void copyTo(
            StorageCollection<FROM_T> fromCollection,
            StorageCollection<TO_T> toCollection,
            Function<FROM_T, TO_T> mapFunction

    ) {
        int skipAmount = 0;
        List<TO_T> newValues;
        do {
            newValues = fromCollection.findAll(skipAmount, migrationPageSize)
                    .map(mapFunction)
                    .collect(Collectors.toList());

            skipAmount += newValues.size();

            int inserted = toCollection.bulkWrite(newValues);
            if (inserted != newValues.size()) {
                throw new RuntimeException("Lost records!");
            }
        } while (newValues.size() >= migrationPageSize);
    }

    public abstract NEW_T migrateEntity(OLD_T oldObject);
    public abstract OLD_T rollbackEntity(NEW_T newObject);

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public void setNewCollectionName(String newCollectionName) {
        this.newCollectionName = newCollectionName;
    }

    public String getOldCollectionName() {
        return oldCollectionName;
    }

    public void setOldCollectionName(String oldCollectionName) {
        this.oldCollectionName = oldCollectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Boolean getRemoveOld() {
        return removeOld;
    }

    public void setRemoveOld(Boolean removeOld) {
        this.removeOld = removeOld;
    }

    public int getMigrationPageSize() {
        return migrationPageSize;
    }

    public void setMigrationPageSize(int migrationPageSize) {
        this.migrationPageSize = migrationPageSize;
    }
}
