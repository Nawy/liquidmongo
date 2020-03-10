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
import org.bson.types.ObjectId;
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
import static org.junit.jupiter.api.Assertions.*;

public class NormalMigrationTest {
    private TestMongoServer testMongoServer;
    private MongoClient mongoClient;

    private JacksonMongoCollection<AdminVersion1> adminCollectionV1;
    private JacksonMongoCollection<UserVersion1> userCollectionV1;

    private JacksonMongoCollection<AdminVersion2> adminCollectionV2;
    private JacksonMongoCollection<UserVersion2> userCollectionV2;

    private JacksonMongoCollection<AdminVersion3> adminCollectionV3;

    private String DATABASE_NAME = "core";
    private String ADMIN_COLLECTION_NAME_V1 = "adminus";
    private String ADMIN_COLLECTION_NAME_V2 = "adminus";
    private String ADMIN_COLLECTION_NAME_V3 = "admin";

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
            "3",
            new CredentialsOld("stal", "123456")
    );

    private final static UserVersion1 USER_V1_1 = new UserVersion1(null, "User 1");

    private final Liquidmongo LIQUIDMONGO;

    private List<AdminVersion2> adminsV2;
    private List<AdminVersion3> adminsV3;

    private List<UserVersion2> usersV2;

    public NormalMigrationTest() {
        this.testMongoServer = new TestMongoServer();
        this.mongoClient = this.testMongoServer.getClient();
        this.adminCollectionV1 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        ADMIN_COLLECTION_NAME_V1,
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
                        ADMIN_COLLECTION_NAME_V2,
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

        this.adminCollectionV3 = JacksonMongoCollection
                .builder()
                .build(
                        mongoClient,
                        DATABASE_NAME,
                        ADMIN_COLLECTION_NAME_V3,
                        AdminVersion3.class
                );



        this.LIQUIDMONGO = new Liquidmongo();
        this.LIQUIDMONGO.setStorageAdapter(new DefaultMongoStorage(this.mongoClient, DATABASE_NAME));
        this.LIQUIDMONGO.addMigrations(
                new Migration(VERSION_2)
                        .addStep(new Admin1to2Migration(1))
                        .addStep(new User1toUser2Migration(2)),

                new Migration(MigrationVersions.VERSION_3)
                        .addStep(new Admin2to3Migration(1))
        );
        LIQUIDMONGO.prepared();
    }

    @AfterEach
    public void cleanUp() {
        // preparation
        if (this.adminsV2 != null) {
            this.adminsV2.forEach(admin -> adminCollectionV2.removeById(admin.getId()));
        }
        if (this.adminsV3 != null) {
            this.adminsV3.forEach(admin -> adminCollectionV3.removeById(admin.getId()));
        }
        if (this.usersV2 != null) {
            this.usersV2.forEach(user -> userCollectionV2.removeById(user.getId()));
        }
    }

    @Test
    void migrationUp_onOneStep() {
        // preparation
		this.adminCollectionV1.insert(ADMIN_V1_1, ADMIN_V1_2);
		this.userCollectionV1.insert(USER_V1_1);

		Map<ObjectId, AdminVersion1> adminsV1 =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(AdminVersion1::getId, Function.identity()));

        Map<Object, UserVersion1> usersV1 =  StreamSupport
                .stream(
                        userCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(UserVersion1::getId, Function.identity()));


        // invoking
        LIQUIDMONGO.setCurrentVersion(-1);
        LIQUIDMONGO.setTargetVersion(VERSION_2);
        LIQUIDMONGO.execute();

        // checking
        this.adminsV2 =  StreamSupport
                .stream(
                        adminCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(this.adminsV2.size(), 2);
        adminsV2.forEach(admin -> {
            AdminVersion1 lastAdmin = adminsV1.get(admin.getId());
            Objects.requireNonNull(lastAdmin);

            assertEquals(admin.getId(), lastAdmin.getId());
            assertEquals(admin.getEmail(), lastAdmin.getEmail());
            assertEquals(admin.getLevel(), lastAdmin.getLevelName());

            String[] names = lastAdmin.getName().split(" ");
            assertEquals(admin.getFirstName(), names[0]);
            assertEquals(admin.getLastName(), names[1]);
        });

        // check users
        this.usersV2 = StreamSupport
                .stream(
                        userCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(this.usersV2.size(), 1);
        usersV2.forEach(user -> {
            UserVersion1 lastAdmin = usersV1.get(user.getId());
            assertEquals(user.getId(), lastAdmin.getId());
            assertEquals(user.getLogin(), lastAdmin.getName());
            assertTrue(user.getGuest());
        });
    }

    @Test
    void migrationUp_onTwoSteps() {
        // preparation
        this.adminCollectionV1.insert(ADMIN_V1_1, ADMIN_V1_2);
        this.userCollectionV1.insert(USER_V1_1);

        Map<Object, AdminVersion1> adminsV1 =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(AdminVersion1::getId, Function.identity()));

        Map<Object, UserVersion1> usersV1 =  StreamSupport
                .stream(
                        userCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(UserVersion1::getId, Function.identity()));

        LIQUIDMONGO.setCurrentVersion(-1);
        LIQUIDMONGO.setTargetVersion(MigrationVersions.VERSION_3);
        LIQUIDMONGO.execute();

        // checking
        this.adminsV3 =  StreamSupport
                .stream(
                        adminCollectionV3.find().spliterator(),
                        false
                ).collect(Collectors.toList());


        assertEquals(this.adminsV3.size(), 2);

        adminsV3.forEach(admin -> {
            AdminVersion1 lastAdmin = adminsV1.get(admin.getId());
            Objects.requireNonNull(lastAdmin);

            assertEquals(admin.getId(), lastAdmin.getId());
            assertEquals(admin.getEmail(), lastAdmin.getEmail());
            assertEquals(admin.getLevel(), Integer.parseInt(lastAdmin.getLevelName()));

            String[] names = lastAdmin.getName().split(" ");
            assertEquals(admin.getFirstName(), names[0]);
            assertEquals(admin.getLastName(), names[1]);
            assertEquals(admin.getCredentials().getLogin(), lastAdmin.getCredentials().getLogin());
            assertEquals(admin.getCredentials().getPassword(), lastAdmin.getCredentials().getPassword());

            final String referralLink = "referral_link_of_" + lastAdmin.getCredentials().getLogin();
            if (admin.getLevel() > 2) {
                assertEquals(admin.getCredentials().getReferralLink(), referralLink);
            } else {
                assertNull(admin.getCredentials().getReferralLink());
            }
        });

        // check users
        this.usersV2 = StreamSupport
                .stream(
                        userCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(this.usersV2.size(), 1);
        usersV2.forEach(user -> {
            UserVersion1 lastAdmin = usersV1.get(user.getId());
            assertEquals(user.getId(), lastAdmin.getId());
            assertEquals(user.getLogin(), lastAdmin.getName());
            assertTrue(user.getGuest());
        });
    }

    @Test
    void migrationDown_onOneStep() {
        // preparation
        this.adminCollectionV1.insert(ADMIN_V1_1, ADMIN_V1_2);
        this.userCollectionV1.insert(USER_V1_1);

        Map<Object, AdminVersion1> adminsV1 =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(AdminVersion1::getId, Function.identity()));

        Map<Object, UserVersion1> usersV1 =  StreamSupport
                .stream(
                        userCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(UserVersion1::getId, Function.identity()));

        final int futureVersion = MigrationVersions.VERSION_3;
        final int preVersion = MigrationVersions.VERSION_2;

        LIQUIDMONGO.setCurrentVersion(-1);
        LIQUIDMONGO.setTargetVersion(futureVersion);
        LIQUIDMONGO.execute();

        // down
        LIQUIDMONGO.setCurrentVersion(futureVersion);
        LIQUIDMONGO.setTargetVersion(preVersion);
        LIQUIDMONGO.execute();

        // checking
        this.adminsV2 =  StreamSupport
                .stream(
                        adminCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(this.adminsV2.size(), 2);
        adminsV2.forEach(admin -> {
            AdminVersion1 lastAdmin = adminsV1.get(admin.getId());
            Objects.requireNonNull(lastAdmin);

            assertEquals(admin.getId(), lastAdmin.getId());
            assertEquals(admin.getEmail(), lastAdmin.getEmail());
            assertEquals(admin.getLevel(), lastAdmin.getLevelName());

            String[] names = lastAdmin.getName().split(" ");
            assertEquals(admin.getFirstName(), names[0]);
            assertEquals(admin.getLastName(), names[1]);
        });

        // check users
        this.usersV2 = StreamSupport
                .stream(
                        userCollectionV2.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(this.usersV2.size(), 1);
        usersV2.forEach(user -> {
            UserVersion1 lastAdmin = usersV1.get(user.getId());
            assertEquals(user.getId(), lastAdmin.getId());
            assertEquals(user.getLogin(), lastAdmin.getName());
            assertTrue(user.getGuest());
        });
    }

    @Test
    void migrationDown_towardStart() {
        // preparation
        this.adminCollectionV1.insert(ADMIN_V1_1, ADMIN_V1_2);
        this.userCollectionV1.insert(USER_V1_1);

        Map<Object, AdminVersion1> adminsV1Map =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(AdminVersion1::getId, Function.identity()));

        Map<Object, UserVersion1> usersV1Map =  StreamSupport
                .stream(
                        userCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toMap(UserVersion1::getId, Function.identity()));

        LIQUIDMONGO.setCurrentVersion(-1);
        LIQUIDMONGO.setTargetVersion(MigrationVersions.VERSION_3);
        LIQUIDMONGO.execute();

        // down
        LIQUIDMONGO.setCurrentVersion(MigrationVersions.VERSION_3);
        LIQUIDMONGO.setTargetVersion(-1);
        LIQUIDMONGO.execute();

        // checking
        List<AdminVersion1> adminsV1 =  StreamSupport
                .stream(
                        adminCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(adminsV1.size(), 2);
        adminsV1.forEach(admin -> {
            AdminVersion1 lastAdmin = adminsV1Map.get(admin.getId());
            Objects.requireNonNull(lastAdmin);

            assertEquals(admin.getId(), lastAdmin.getId());
            assertEquals(admin.getEmail(), lastAdmin.getEmail());
            assertEquals(admin.getName(), lastAdmin.getName());
            assertEquals(admin.getLevelName(), lastAdmin.getLevelName());
        });

        // check users
        List<UserVersion1> usersV1 = StreamSupport
                .stream(
                        userCollectionV1.find().spliterator(),
                        false
                ).collect(Collectors.toList());

        assertEquals(usersV1.size(), 1);
        usersV1.forEach(user -> {
            UserVersion1 lastAdmin = usersV1Map.get(user.getId());
            assertEquals(user.getId(), lastAdmin.getId());
            assertEquals(user.getName(), lastAdmin.getName());

        });
    }
}
