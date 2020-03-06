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

    public MigrationExecutor findCurrentPosition(int currentVersion) {
        if (getVersion() == currentVersion) {
            return this;
        }

        if (currentVersion > getVersion()) {
            if (newMigration == null) {
                throw new UnreachebleMigrationVersionException();
            }
            return newMigration.findCurrentPosition(currentVersion);
        } else {
            if (oldMigration == null) {
                return this;
            }
            return oldMigration.findCurrentPosition(currentVersion);
        }
    }

    public int startMigrationTo(int targetVersion, int currentVersion, StorageAdapter storageAdapter) {
        final int version = getVersion();
        if (version == currentVersion) {
            return version;
        }
        migration.migrate(storageAdapter);
        return migrateTo(targetVersion, storageAdapter);
    }

    public int migrateTo(int targetVersion, StorageAdapter storageAdapter) {
        final int version = getVersion();

        if (targetVersion == version) {
            return version;
        }

        if (targetVersion > version) {
            migration.migrate(storageAdapter);

            if (newMigration == null) {
                throw new UnreachebleMigrationVersionException();
            }

            return newMigration.migrateTo(targetVersion, storageAdapter);
        } else {
            migration.rollback(storageAdapter);

            if (oldMigration == null) {
                throw new UnreachebleMigrationVersionException();
            }
            return oldMigration.migrateTo(targetVersion, storageAdapter);
        }
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
