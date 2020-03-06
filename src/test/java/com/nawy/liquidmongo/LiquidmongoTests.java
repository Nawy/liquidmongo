package com.nawy.liquidmongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.nawy.liquidmongo.migration.*;
import com.nawy.liquidmongo.model.AdminVersion1;
import com.nawy.liquidmongo.model.CredentialsOld;
import com.nawy.liquidmongo.model.UserVersion1;
import com.nawy.liquidmongo.storage.DefaultMongoStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mongojack.JacksonMongoCollection;

import static com.nawy.liquidmongo.migration.MigrationVersions.VERSION_2;

class LiquidmongoTests {

	private MongoClient mongoClient;
	private JacksonMongoCollection<AdminVersion1> adminCollection;
	private JacksonMongoCollection<UserVersion1> userCollection;
	private String DATABASE_NAME = "core";
	private String ADMIN_COLLECTION_NAME = "adminus";
	private String USERS_COLLECTION_NAME = "users";

	private static final AdminVersion1 ADMIN_V1_1 = new AdminVersion1(
			null,
			"John Wick",
			"John@example.com",
			"1",
			new CredentialsOld("john.w", "123456")
	);

	private static final AdminVersion1 ADMIN_V1_2 = new AdminVersion1(
			null,
			"Sylvester Stallone",
			"sylvester@example.com",
			"1",
			new CredentialsOld("stal", "123456")
	);

	private final static UserVersion1 USER_V1_1 = new UserVersion1(null, "User 1");

	public LiquidmongoTests() {
		this.mongoClient = MongoClients.create("mongodb://localhost:27017");
		this.adminCollection = JacksonMongoCollection
				.builder()
				.build(
						mongoClient,
						DATABASE_NAME,
						ADMIN_COLLECTION_NAME,
						AdminVersion1.class
				);

		this.userCollection = JacksonMongoCollection
				.builder()
				.build(
						mongoClient,
						DATABASE_NAME,
						USERS_COLLECTION_NAME,
						UserVersion1.class
				);
	}

	@BeforeEach
	void init() {

	}

	@Test
	void test1() {
//		this.adminCollection.insert(ADMIN_V1_1, ADMIN_V1_2);
//		this.userCollection.insert(USER_V1_1);

		Liquidmongo liquidmongo = new Liquidmongo();
		liquidmongo.setStorageAdapter(new DefaultMongoStorage(this.mongoClient, DATABASE_NAME));
		liquidmongo.setCurrentVersion(0);
		liquidmongo.addMigration(

				new Migration(VERSION_2)
					.addStep(new Admin1to2Migration(1))
					.addStep(new User1toUser2Migration(2)),

				new Migration(MigrationVersions.VERSION_3)
						.addStep(new Admin2to3Migration(1))
		);

		liquidmongo.setTargetVersion(VERSION_2);
		liquidmongo.afterPropertiesSet();
		liquidmongo.execute();
	}

}
