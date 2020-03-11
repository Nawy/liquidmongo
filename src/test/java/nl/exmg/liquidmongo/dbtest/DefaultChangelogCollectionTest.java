package nl.exmg.liquidmongo.dbtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mongodb.client.MongoClient;
import java.time.LocalDateTime;
import nl.exmg.liquidmongo.dbtest.help.TestMongoServer;
import nl.exmg.liquidmongo.model.MigrationDoc;
import nl.exmg.liquidmongo.storage.DefaultChangelogCollection;
import org.junit.jupiter.api.Test;

public class DefaultChangelogCollectionTest {
  private TestMongoServer testMongoServer;
  private MongoClient mongoClient;
  private DefaultChangelogCollection defaultChangelogCollection;

  private final String DATABASE_NAME = "test";
  private final String COLLECTION_NAME = "migrationChangelog";

  public DefaultChangelogCollectionTest() {
    this.testMongoServer = new TestMongoServer();
    this.mongoClient = this.testMongoServer.getClient();
    this.defaultChangelogCollection = new DefaultChangelogCollection(
        mongoClient,
        DATABASE_NAME,
        COLLECTION_NAME
    );
  }

  @Test
  public void test() {
    if (this.defaultChangelogCollection.isCollectionExists()) {
      defaultChangelogCollection.createCollection();
    }

    MigrationDoc doc1 = new MigrationDoc(
        null,
        "name1",
        LocalDateTime.now(),
        1,
        2,
        DATABASE_NAME,
        DATABASE_NAME,
        COLLECTION_NAME,
        COLLECTION_NAME
    );
    MigrationDoc doc2 = new MigrationDoc(
        null,
        "name1",
        LocalDateTime.now().plusDays(1),
        2,
        3,
        DATABASE_NAME,
        DATABASE_NAME,
        COLLECTION_NAME,
        COLLECTION_NAME
    );

    defaultChangelogCollection.insert(doc1);
    defaultChangelogCollection.insert(doc2);

    MigrationDoc last = defaultChangelogCollection.getLastStep();
    assertEquals(last.getFromVersion(), doc2.getFromVersion());
  }
}
