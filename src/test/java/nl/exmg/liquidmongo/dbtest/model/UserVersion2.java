package nl.exmg.liquidmongo.dbtest.model;

import org.bson.types.ObjectId;
import org.mongojack.Id;

public class UserVersion2 {
    @Id
    private ObjectId id;
    private String login;
    private Boolean isGuest;

    public UserVersion2(ObjectId id, String login, Boolean isGuest) {
        this.id = id;
        this.login = login;
        this.isGuest = isGuest;
    }

    public UserVersion2() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Boolean getGuest() {
        return isGuest;
    }

    public void setGuest(Boolean guest) {
        isGuest = guest;
    }
}
