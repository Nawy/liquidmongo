package nl.exmg.liquidmongo.migration;

import nl.exmg.liquidmongo.storage.StorageAdapter;
import nl.exmg.liquidmongo.storage.StorageCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class MigrateExecutorTest {

    private final int VERSION_1 = 1;
    private final int VERSION_2 = 2;
    private final int VERSION_3 = 3;

    final MigrationStep MIGRATION_V1_S1 = Mockito.mock(MigrationStep.class);
    final MigrationStep MIGRATION_V1_S2 = Mockito.mock(MigrationStep.class);
    final MigrationStep MIGRATION_V1_S3 = Mockito.mock(MigrationStep.class);

    final MigrationStep MIGRATION_V2_S1 = Mockito.mock(MigrationStep.class);
    final MigrationStep MIGRATION_V2_S2 = Mockito.mock(MigrationStep.class);

    final MigrationStep MIGRATION_V3_S1 = Mockito.mock(MigrationStep.class);


    final Migration MIGRATION_1 = new Migration(VERSION_1)
            .addStep(MIGRATION_V1_S1)
            .addStep(MIGRATION_V1_S2)
            .addStep(MIGRATION_V1_S3);

    final Migration MIGRATION_2 = new Migration(VERSION_2)
            .addStep(MIGRATION_V2_S1)
            .addStep(MIGRATION_V2_S2);

    final Migration MIGRATION_3 = new Migration(VERSION_3)
            .addStep(MIGRATION_V3_S1);


    final MigrationExecutor MIGRATION_EXEC = MigrationExecutor.init(MIGRATION_1)
            .to(MIGRATION_2)
            .to(MIGRATION_3);

    final StorageAdapter STORAGE_ADAPTER = Mockito.mock(StorageAdapter.class);
    final StorageCollection COLLECTION = Mockito.mock(StorageCollection.class);

    public MigrateExecutorTest() {
        when(MIGRATION_V1_S1.getOrder()).thenReturn(1);
        when(MIGRATION_V1_S2.getOrder()).thenReturn(2);
        when(MIGRATION_V1_S3.getOrder()).thenReturn(3);

        when(MIGRATION_V2_S1.getOrder()).thenReturn(1);
        when(MIGRATION_V2_S2.getOrder()).thenReturn(2);

        when(MIGRATION_V3_S1.getOrder()).thenReturn(1);

        when(STORAGE_ADAPTER.getCollection(anyString(), anyObject())).thenReturn(COLLECTION);
        when(COLLECTION.findAll(anyInt(), anyInt())).thenReturn(Stream.of(new Object(), new Object()));
        when(COLLECTION.bulkWrite(anyObject())).thenReturn(2);
    }

    @Test
    public void should_startMigrationTo_migrateUpToHighestVersion() {
        final int currentVersion = MIGRATION_EXEC.getMigration(-1)
                .migrateTo(VERSION_3, STORAGE_ADAPTER);

        // checking

        InOrder inOrder = inOrder(
                MIGRATION_V1_S1,
                MIGRATION_V1_S2,
                MIGRATION_V1_S3,
                MIGRATION_V2_S1,
                MIGRATION_V2_S2,
                MIGRATION_V3_S1
        );
        Assertions.assertEquals(currentVersion, VERSION_3);

        inOrder.verify(MIGRATION_V1_S1).migrate(any());
        inOrder.verify(MIGRATION_V1_S2).migrate(any());
        inOrder.verify(MIGRATION_V1_S3).migrate(any());
        inOrder.verify(MIGRATION_V2_S1).migrate(any());
        inOrder.verify(MIGRATION_V2_S2).migrate(any());
        inOrder.verify(MIGRATION_V3_S1).migrate(any());

        // never
        inOrder.verify(MIGRATION_V3_S1, never()).rollback(any());
        inOrder.verify(MIGRATION_V2_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V2_S1, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S3, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S1, never()).rollback(any());

    }

    @Test
    public void should_startMigrationTo_migrateDownToLowestVersion() {
        final int currentVersion = MIGRATION_EXEC.getMigration(VERSION_3)
                .migrateTo(-1, STORAGE_ADAPTER);

        // checking
        InOrder inOrder = inOrder(
                MIGRATION_V3_S1,
                MIGRATION_V2_S2,
                MIGRATION_V2_S1,
                MIGRATION_V1_S3,
                MIGRATION_V1_S2,
                MIGRATION_V1_S1
        );
        Assertions.assertEquals(currentVersion, VERSION_1);

        inOrder.verify(MIGRATION_V3_S1).rollback(any());
        inOrder.verify(MIGRATION_V2_S1).rollback(any());
        inOrder.verify(MIGRATION_V2_S2).rollback(any());

        inOrder.verify(MIGRATION_V1_S1).rollback(any());
        inOrder.verify(MIGRATION_V1_S2).rollback(any());
        inOrder.verify(MIGRATION_V1_S3).rollback(any());

        // never
        inOrder.verify(MIGRATION_V3_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V2_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V2_S2, never()).migrate(any());

        inOrder.verify(MIGRATION_V1_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S2, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S3, never()).migrate(any());
    }

    @Test
    public void should_startMigrationTo_migrateUpTo1() {
        final int currentVersion = MIGRATION_EXEC.getMigration(VERSION_2)
                .migrateTo(VERSION_3, STORAGE_ADAPTER);

        // checking
        InOrder inOrder = inOrder(
                MIGRATION_V3_S1,
                MIGRATION_V2_S2,
                MIGRATION_V2_S1,
                MIGRATION_V1_S3,
                MIGRATION_V1_S2,
                MIGRATION_V1_S1
        );

        Assertions.assertEquals(currentVersion, VERSION_3);
        inOrder.verify(MIGRATION_V3_S1).migrate(any()); // main action
        inOrder.verify(MIGRATION_V2_S2, never()).migrate(any());
        inOrder.verify(MIGRATION_V2_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S3, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S2, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S1, never()).migrate(any());

        inOrder.verify(MIGRATION_V3_S1, never()).rollback(any());
        inOrder.verify(MIGRATION_V2_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V2_S1, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S3, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S1, never()).rollback(any());
    }

    @Test
    public void should_startMigrationTo_migrateDownTo1() {
        final int currentVersion = MIGRATION_EXEC
                .getMigration(VERSION_3)
                .migrateTo(VERSION_2, STORAGE_ADAPTER);

        InOrder inOrder = inOrder(
                MIGRATION_V3_S1,
                MIGRATION_V2_S2,
                MIGRATION_V2_S1,
                MIGRATION_V1_S3,
                MIGRATION_V1_S2,
                MIGRATION_V1_S1
        );

        // checking
        Assertions.assertEquals(currentVersion, VERSION_2);

        inOrder.verify(MIGRATION_V3_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V2_S2, never()).migrate(any());
        inOrder.verify(MIGRATION_V2_S1, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S3, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S2, never()).migrate(any());
        inOrder.verify(MIGRATION_V1_S1, never()).migrate(any());

        inOrder.verify(MIGRATION_V3_S1).rollback(any()); // main action
        inOrder.verify(MIGRATION_V2_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V2_S1, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S3, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S2, never()).rollback(any());
        inOrder.verify(MIGRATION_V1_S1, never()).rollback(any());

    }

    @Test
    public void should_migrateTo_migrateInSmallList() {
        final int currentVersion = VERSION_2;
        final int targetVersion = VERSION_3;

        final MigrationExecutor executor = MigrationExecutor
                .init(MIGRATION_2)
                .to(MIGRATION_3);

        final int version = executor
                .getMigration(currentVersion)
                .migrateTo(targetVersion, STORAGE_ADAPTER);

        Assertions.assertEquals(version, VERSION_3);
        verify(MIGRATION_V3_S1).migrate(any());
        verify(MIGRATION_V3_S1, never()).rollback(any());
    }
}
