package nl.exmg.liquidmongo.storage;

import nl.exmg.liquidmongo.model.MigrationDoc;

/**
 * The Contract of change log storage
 */
public interface ChangelogCollection {
    boolean isCollectionExists();

    void createCollection();

    void insert(MigrationDoc migrationDoc);

    MigrationDoc getLastStep();
}
