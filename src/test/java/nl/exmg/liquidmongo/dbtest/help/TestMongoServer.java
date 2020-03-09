package nl.exmg.liquidmongo.dbtest.help;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;

public class TestMongoServer {
    private MongoClient client;
    private MongoServer server;

    public TestMongoServer() {
        server = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = server.bind();

        client = MongoClients.create("mongodb://" +serverAddress.getHostName() + ":" + serverAddress.getPort());
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoServer getServer() {
        return server;
    }

    public void shutdown() {
        server.shutdown();
    }
}
