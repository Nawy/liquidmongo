package nl.exmg.liquidmongo.migration;

import nl.exmg.liquidmongo.storage.StorageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Technical method, do plan to use it for the annotation-based creation
     * @param migrationSteps
     */
    public void setMigrationSteps(List<MigrationStep> migrationSteps) {
        Objects.requireNonNull(migrationSteps);
        this.migrationSteps = migrationSteps;
    }

    public Migration addStep(MigrationStep step) {
        migrationSteps.add(step);
        return this;
    }
}
