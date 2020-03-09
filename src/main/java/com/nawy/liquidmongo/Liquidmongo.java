package com.nawy.liquidmongo;

import com.nawy.liquidmongo.migration.Migration;
import com.nawy.liquidmongo.migration.MigrationExecutor;
import com.nawy.liquidmongo.storage.StorageAdapter;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Liquidmongo implements InitializingBean {

	private StorageAdapter storageAdapter;
	private List<Migration> migrations = new ArrayList<>();
	private int targetVersion;
	private int currentVersion;

	private MigrationExecutor executor;


	@Override
	public void afterPropertiesSet() {
		migrations.stream()
				.sorted(Comparator.comparingInt(Migration::getVersion))
				.forEach(this::addMigrationToExecutor);

		this.execute();
	}

	public void execute() {
		this.executor
				.getMigration(currentVersion)
				.migrateTo(targetVersion, storageAdapter);
	}

	private void addMigrationToExecutor(Migration migration) {
		if (executor == null) {
			this.executor = MigrationExecutor.init(migration);
		} else {
			this.executor.to(migration);
		}
	}

	public void setStorageAdapter(StorageAdapter storageAdapter) {
		this.storageAdapter = storageAdapter;
	}

	public void setTargetVersion(int targetVersion) {
		this.targetVersion = targetVersion;
	}

	public void addMigration(Migration... migration) {
		this.migrations.addAll(Arrays.asList(migration));
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}
}
