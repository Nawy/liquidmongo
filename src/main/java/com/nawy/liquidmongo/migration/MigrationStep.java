package com.nawy.liquidmongo.migration;

import com.nawy.liquidmongo.storage.StorageAdapter;
import com.nawy.liquidmongo.storage.StorageCollection;

import java.util.List;
import java.util.stream.Collectors;

public abstract class MigrationStep <OLD_T, NEW_T> {

    private int pageSize = 100;

    private int order;
    private String uniqueName;
    private String newCollectionName;
    private String oldCollectionName;
    private String collectionName;
    private Boolean copyToNew = true;
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
        this.order = order;
        this.uniqueName = oldCollectionName + "_" + newCollectionName;
        this.collectionName = oldCollectionName;
    }

    public MigrationStep(
            int order,
            String collectionName,
            Class<OLD_T> oldClass,
            Class<NEW_T> newClass
    ) {
        this.order = order;
        this.uniqueName = "migration_" + collectionName;
        this.collectionName = collectionName;
    }

    public void migrate(StorageAdapter storage) {

        StorageCollection<OLD_T> collection = storage.getCollection(collectionName, oldClass);
        StorageCollection<NEW_T> newCollection = storage.getCollection(newCollectionName, newClass);

        int skipAmount = 0;
        List<NEW_T> newValues;
        do {
            newValues = this.migrateList(collection, skipAmount);

            skipAmount += newValues.size();

            if (copyToNew) {
                int inserted = newCollection.bulkWrite(newValues);
                if (inserted != newValues.size()) {
                    throw new RuntimeException("Lost records!");
                }
            } else {
                // TODO
            }
        } while (newValues.size() >= pageSize);

    }

    public void rollback(StorageAdapter storage) {

        StorageCollection<NEW_T> collection = storage.getCollection(newCollectionName, newClass);
        StorageCollection<OLD_T> oldCollection = storage.getCollection(oldCollectionName, oldClass);

        if (copyToNew && !removeOld) {
            oldCollection.drop();
        }

        int skipAmount = 0;
        List<OLD_T> newValues;
        do {
            newValues = this.rollbackList(collection, skipAmount);

            skipAmount += newValues.size();

            if (copyToNew) {
                int inserted = oldCollection.bulkWrite(newValues);
                if (inserted != newValues.size()) {
                    throw new RuntimeException("Lost records!");
                }
            } else {
                // TODO
            }
        } while (newValues.size() >= pageSize);
    }

    private List<NEW_T> migrateList(StorageCollection<OLD_T> collection, int skipAmount) {
        return collection.findAll(skipAmount, pageSize)
                .map(this::migrateEntity)
                .collect(Collectors.toList());
    }

    private List<OLD_T> rollbackList(StorageCollection<NEW_T> collection, int skipAmount) {
        return collection.findAll(skipAmount, pageSize)
                .map(this::rollbackEntity)
                .collect(Collectors.toList());
    }

    public void clean(StorageCollection collection) {
        if (removeOld) {
            collection.drop();
        }
    }

    abstract NEW_T migrateEntity(OLD_T oldObject);
    abstract OLD_T rollbackEntity(NEW_T newObject);

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

    public String getNewCollectionName() {
        return newCollectionName;
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

    public Boolean getCopyToNew() {
        return copyToNew;
    }

    public void setCopyToNew(Boolean copyToNew) {
        this.copyToNew = copyToNew;
    }

    public Boolean getRemoveOld() {
        return removeOld;
    }

    public void setRemoveOld(Boolean removeOld) {
        this.removeOld = removeOld;
    }
}
