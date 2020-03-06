package com.nawy.liquidmongo;

import com.nawy.liquidmongo.migration.*;
import org.junit.jupiter.api.Test;

import static com.nawy.liquidmongo.migration.MigrationVersions.VERSION_2;

class LiquidmongoTests {

	@Test
	void test1() {

		Liquidmongo liquidmongo = new Liquidmongo();
		liquidmongo.addMigration(

				new Migration(VERSION_2)
					.addStep(new Admin1to2Migration(1))
					.addStep(new User1toUser2Migration(2)),

				new Migration(MigrationVersions.VERSION_3)
						.addStep(new Admin2to3Migration(1))
		);

		liquidmongo.setTargetVersion(VERSION_2);
	}

}
