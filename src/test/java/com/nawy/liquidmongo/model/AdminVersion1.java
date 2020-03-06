package com.nawy.liquidmongo.model;

import org.mongojack.Id;

public class AdminVersion1 {

    @Id
    private String id;
    private String name;
    private String email;
    private String levelName;
    private CredentialsOld credentials;

    public AdminVersion1(String id, String name, String email, String levelName, CredentialsOld credentials) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.levelName = levelName;
        this.credentials = credentials;
    }

    public AdminVersion1() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public CredentialsOld getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsOld credentials) {
        this.credentials = credentials;
    }
}
