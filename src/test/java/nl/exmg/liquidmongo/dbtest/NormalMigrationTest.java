package nl.exmg.liquidmongo.dbtest;

import com.mongodb.client.MongoClient;
import nl.exmg.liquidmongo.Liquidmongo;
import nl.exmg.liquidmongo.dbtest.help.TestMongoServer;
import nl.exmg.liquidmongo.dbtest.migration.Admin1to2Migration;
import nl.exmg.liquidmongo.dbtest.migration.Admin2to3Migration;
import nl.exmg.liquidmongo.dbtest.migration.User1toUser2Migration;
import nl.exmg.liquidmongo.dbtest.model.*;
import nl.exmg.liquidmongo.migration.Migration;
import nl.exmg.liquidmongo.storage.DefaultMongoStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mongojack.JacksonMongoCollection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static nl.exmg.liquidmongo.dbtest.MigrationVersions.VERSION_2;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NormalMigrationTest {
    private TestMongoServer testMongoServer;
    private MongoClient mongoClient;

    private JacksonMongoCollection<AdminVersion1> adminCollectionV1;
    private JacksonMongoCollection<UserVersion1> userCollectionV1;

    private JacksonMongoCollection<AdminVersion2> adminCollectionV2;
    private JacksonMongoCollection<UserVersion2> userCollectionV2;

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

    public NormalMigrationTest() {
        this.testMongoServer = new TestMongoServer();
        this.mongoClient = this.testMongoServer.getClient();
        this.adminCollectionV1 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        ADMIN_COLLECTION_NAME,
                        AdminVersion1.class
                );

        this.userCollectionV1 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        USERS_COLLECTION_NAME,
                        UserVersion1.class
                );

        this.adminCollectionV2 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        ADMIN_COLLECTION_NAME,
                        AdminVersion2.class
                );

        this.userCollectionV2 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        USERS_COLLECTION_NAME,
                        UserVersion2.class
                );
    }

    private List<AdminVersion2> adminsV2;

    @Test
    void simpleTest() {
        // preparation
		this.adminCollectionV1.insert(ADMIN_V1_1, ADMIN_V1_2);
		this.userCollectionV1.insert(USER_V1_1);

		Map<String, AdminVersion1> adminsV1 =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(AdminVersion1::getId, Function.identity()));

        // invoke
        Liquidmongo liquidmongo = new Liquidmongo();
        liquidmongo.setStorageAdapter(new DefaultMongoStorage(this.mongoClient, DATABASE_NAME));
        liquidmongo.setCurrentVersion(-1);
        liquidmongo.addMigrations(
                new Migration(VERSION_2)
                        .addStep(new Admin1to2Migration(1))
                        .addStep(new User1toUser2Migration(2)),

                new Migration(MigrationVersions.VERSION_3)
                        .addStep(new Admin2to3Migration(1))
        );

        // checking
        liquidmongo.setTargetVersion(VERSION_2);
        liquidmongo.afterPropertiesSet();

        this.adminsV2 =  StreamSupport
                .stream(
                        adminCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        this.adminsV2.forEach(admin -> {
            AdminVersion1 lastAdmin = adminsV1.get(admin.getId());
            Objects.requireNonNull(lastAdmin);

            assertEquals(admin.getId(), lastAdmin.getId());
            assertEquals(admin.getEmail(), lastAdmin.getEmail());
            assertEquals(admin.getLevel(), lastAdmin.getLevelName());

            String[] names = lastAdmin.getName().split(" ");
            assertEquals(admin.getFirstName(), names[0]);
            assertEquals(admin.getLastName(), names[1]);
        });
    }
}
