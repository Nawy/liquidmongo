package com.nawy.liquidmongo.model;

import org.mongojack.Id;

public class AdminVersion3 {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private int level;
    private CredentialsNew credentials;

    public AdminVersion3(String id, String firstName, String lastName, String email, int level, CredentialsNew credentials) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.level = level;
        this.credentials = credentials;
    }

    public AdminVersion3() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CredentialsNew getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsNew credentials) {
        this.credentials = credentials;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
