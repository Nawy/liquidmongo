package nl.exmg.liquidmongo.storage;

public interface StorageAdapter {

    <ENTITY_T> StorageCollection<ENTITY_T> getCollection(String collectionName, Class<ENTITY_T> clazz);
}
