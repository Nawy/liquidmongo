package com.nawy.liquidmongo.model;

public class CredentialsNew {
    private String login;
    private String password;
    private String referralLink;

    public CredentialsNew(String login, String password, String referralLink) {
        this.login = login;
        this.password = password;
        this.referralLink = referralLink;
    }

    public CredentialsNew() {
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

    public String getReferralLink() {
        return referralLink;
    }

    public void setReferralLink(String referralLink) {
        this.referralLink = referralLink;
    }
}
