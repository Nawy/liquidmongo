package nl.exmg.liquidmongo.dbtest.model;

import org.mongojack.Id;

public class AdminVersion2 {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String level;
    private CredentialsOld credentials;

    public AdminVersion2(String id, String firstName, String lastName, String email, String level, CredentialsOld credentials) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.level = level;
        this.credentials = credentials;
    }

    public AdminVersion2() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public CredentialsOld getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsOld credentials) {
        this.credentials = credentials;
    }
}
