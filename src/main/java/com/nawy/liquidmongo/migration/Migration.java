package com.nawy.liquidmongo.migration;

import com.nawy.liquidmongo.storage.StorageAdapter;

import java.util.ArrayList;
import java.util.List;

public class Migration {

    private int version;
    private List<MigrationStep> migrationSteps = new ArrayList<>();

    public Migration(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void migrate(StorageAdapter storageAdapter) {
        migrationSteps.forEach(step -> step.migrate(storageAdapter));
    }

    public void rollback(StorageAdapter storageAdapter) {
        migrationSteps.forEach(step -> step.rollback(storageAdapter));
    }

    public List<MigrationStep> getMigrationSteps() {
        return migrationSteps;
    }

    public void setMigrationSteps(List<MigrationStep> migrationSteps) {
        this.migrationSteps = migrationSteps;
    }

    public Migration addStep(MigrationStep step) {
        migrationSteps.add(step);
        return this;
    }
}
