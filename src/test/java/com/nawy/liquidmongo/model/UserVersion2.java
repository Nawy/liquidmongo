package com.nawy.liquidmongo.model;

import org.mongojack.Id;

public class UserVersion2 {
    @Id
    private String id;
    private String login;
    private Boolean isGuest;

    public UserVersion2(String id, String login, Boolean isGuest) {
        this.id = id;
        this.login = login;
        this.isGuest = isGuest;
    }

    public UserVersion2() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
