package nl.exmg.liquidmongo.dbtest.model;

public class CredentialsOld {
    private String login;
    private String password;

    public CredentialsOld(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CredentialsOld() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
