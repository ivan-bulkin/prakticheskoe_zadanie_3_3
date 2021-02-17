package server;

public class AuthMessage {
    private String id;
    private String login;
    private String password;
    public String nick;
    private boolean authentificated = false;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNick() {
//        System.out.println(nick);
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


    public void setAuthentificated(boolean authentificated) {
        this.authentificated = authentificated;
    }
}
