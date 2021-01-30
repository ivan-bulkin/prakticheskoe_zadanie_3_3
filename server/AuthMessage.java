package server;

public class AuthMessage {
    private String login;
    private String password;
    private boolean authentificated = false;

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

    public boolean isAuthentificated() {
        return authentificated;
    }

    public void setAuthentificated(boolean authentificated) {
        this.authentificated = authentificated;
    }
}
