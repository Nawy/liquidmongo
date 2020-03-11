package nl.exmg.liquidmongo.model;

import org.bson.types.ObjectId;
import org.mongojack.Id;

import java.time.LocalDateTime;

public class MigrationDoc {

    @Id
    private ObjectId id;
    private String name;
    private LocalDateTime time;

    private int fromVersion;
    private int toVersion;

    private String fromDatabaseName;
    private String toDatabaseName;

    private String fromCollectionName;
    private String toCollectionName;

    public MigrationDoc() {
    }

    public MigrationDoc(ObjectId id, String name, LocalDateTime time, int fromVersion, int toVersion, String fromDatabaseName, String toDatabaseName, String fromCollectionName, String toCollectionName) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.fromDatabaseName = fromDatabaseName;
        this.toDatabaseName = toDatabaseName;
        this.fromCollectionName = fromCollectionName;
        this.toCollectionName = toCollectionName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(int fromVersion) {
        this.fromVersion = fromVersion;
    }

    public int getToVersion() {
        return toVersion;
    }

    public void setToVersion(int toVersion) {
        this.toVersion = toVersion;
    }

    public String getFromDatabaseName() {
        return fromDatabaseName;
    }

    public void setFromDatabaseName(String fromDatabaseName) {
        this.fromDatabaseName = fromDatabaseName;
    }

    public String getToDatabaseName() {
        return toDatabaseName;
    }

    public void setToDatabaseName(String toDatabaseName) {
        this.toDatabaseName = toDatabaseName;
    }

    public String getFromCollectionName() {
        return fromCollectionName;
    }

    public void setFromCollectionName(String fromCollectionName) {
        this.fromCollectionName = fromCollectionName;
    }

    public String getToCollectionName() {
        return toCollectionName;
    }

    public void setToCollectionName(String toCollectionName) {
        this.toCollectionName = toCollectionName;
    }
}
