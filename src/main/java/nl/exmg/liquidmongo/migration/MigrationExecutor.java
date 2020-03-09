package nl.exmg.liquidmongo.migration;

import nl.exmg.liquidmongo.exception.UnreachebleMigrationVersionException;
import nl.exmg.liquidmongo.storage.StorageAdapter;

import java.util.Objects;

public class MigrationExecutor {

    private Migration migration;
    private int version;
    private MigrationExecutor oldMigration;
    private MigrationExecutor newMigration;

    // lowest and highest
    private MigrationExecutor(int version, MigrationExecutor oldMigration, MigrationExecutor newMigration) {
        this.version = version;
        this.newMigration = newMigration;
        this.oldMigration = oldMigration;
    }

    private MigrationExecutor(Migration migration, MigrationExecutor oldMigration) {
        Objects.requireNonNull(migration);

        this.migration = migration;
        this.version = migration.getVersion();
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
                return MigrationExecutor.lowestMigration(this);
            }
            return oldMigration.getMigration(currentVersion);
        }
    }

    public int migrateTo(int targetVersion, StorageAdapter storageAdapter) {

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
        if (migration != null) {
            migration.migrate(storageAdapter);
        }

        if (newMigration == null || targetVersion == version) {
            return version;
        }

        return newMigration.migrateUp(targetVersion, storageAdapter);

    }

    private int migrateDown(int targetVersion, StorageAdapter storageAdapter) {
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
        return version;
    }

    public static MigrationExecutor init(Migration migration) {
        return new MigrationExecutor(migration, null);
    }

    public MigrationExecutor to(Migration migration) {
        this.newMigration = new MigrationExecutor(migration, this);
        return this.newMigration;
    }

    public static MigrationExecutor lowestMigration(MigrationExecutor nextMigration) {
        return new MigrationExecutor(Integer.MIN_VALUE, null, nextMigration);
    }
}
