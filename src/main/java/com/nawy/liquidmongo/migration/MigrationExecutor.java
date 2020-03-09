package com.nawy.liquidmongo.migration;

import com.nawy.liquidmongo.exception.UnreachebleMigrationVersionException;
import com.nawy.liquidmongo.storage.StorageAdapter;

public class MigrationExecutor {

    private Migration migration;
    private MigrationExecutor oldMigration;
    private MigrationExecutor newMigration;

    private MigrationExecutor(Migration migration, MigrationExecutor oldMigration) {
        this.migration = migration;
        this.oldMigration = oldMigration;
    }

    public MigrationExecutor getMigration(int currentVersion) {
        final int version = getVersion();

        if (version == currentVersion) {
            return this;
        }

        if (currentVersion > version) {
            if (newMigration == null) {
                throw new UnreachebleMigrationVersionException();
            }
            return newMigration.getMigration(currentVersion);
        } else {
            if (oldMigration == null) {
                return this;
            }
            return oldMigration.getMigration(currentVersion);
        }
    }

    public int migrateTo(int targetVersion, StorageAdapter storageAdapter) {
        final int version = getVersion();

        if (targetVersion == version) {
            return version;
        }

        if (targetVersion > version) {
            return migrateUp(targetVersion, storageAdapter);
        } else {
            return migrateDown(targetVersion, storageAdapter);
        }
    }

    private int migrateUp(int targetVersion, StorageAdapter storageAdapter) {
        migration.migrate(storageAdapter);

        final int version = getVersion();
        if (newMigration == null || targetVersion == version) {
            return version;
        }

        return newMigration.migrateUp(targetVersion, storageAdapter);

    }

    private int migrateDown(int targetVersion, StorageAdapter storageAdapter) {
        final int version = getVersion();

        if (targetVersion == version) {
            return version;
        }

        migration.rollback(storageAdapter);

        if (oldMigration == null) {
            return version;
        }

        return oldMigration.migrateDown(targetVersion, storageAdapter);
    }

    public int getVersion() {
        return migration.getVersion();
    }

    public boolean isOldest() {
        return oldMigration == null;
    }

    public static MigrationExecutor init(Migration migration) {
        return new MigrationExecutor(migration, null);
    }

    public MigrationExecutor to(Migration migration) {
        this.newMigration = new MigrationExecutor(migration, this);
        return this.newMigration;
    }

}
