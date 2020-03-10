package nl.exmg.liquidmongo.dbtest.model;

import org.bson.types.ObjectId;
import org.mongojack.Id;

public class UserVersion1 {

    @Id
    private ObjectId id;
    private String name;

    public UserVersion1(ObjectId id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserVersion1() {
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
}
