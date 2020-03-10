package nl.exmg.liquidmongo;

import nl.exmg.liquidmongo.dbtest.migration.Admin1to2Migration;
import nl.exmg.liquidmongo.dbtest.migration.Admin2to3Migration;
import nl.exmg.liquidmongo.dbtest.migration.User1toUser2Migration;
import nl.exmg.liquidmongo.migration.Migration;
import nl.exmg.liquidmongo.dbtest.MigrationVersions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.exmg.liquidmongo.dbtest.MigrationVersions.VERSION_2;

class LiquidmongoTests {



	@BeforeEach
	void init() {

	}

	@Test
	void test1() {

		Liquidmongo liquidmongo = new Liquidmongo();
		liquidmongo.setStorageAdapter(null);
		liquidmongo.setCurrentVersion(-1);
		liquidmongo.addMigrations(
				new Migration(VERSION_2)
					.addStep(new Admin1to2Migration(1))
					.addStep(new User1toUser2Migration(2)),

				new Migration(MigrationVersions.VERSION_3)
						.addStep(new Admin2to3Migration(1))
		);

		liquidmongo.setTargetVersion(VERSION_2);
		liquidmongo.prepared();
	}

}
