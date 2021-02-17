package client;

public class User {
    private int idKlienta;
    private String loginKlienta;
    private String passwordKlienta;
    private String nickKlienta;

    public User(int idKlienta, String loginKlienta, String passwordKlienta, String nickKlienta) {
        this.idKlienta = idKlienta;
        this.loginKlienta = loginKlienta;
        this.passwordKlienta = passwordKlienta;
        this.nickKlienta = nickKlienta;
    }

    public User() {
    }

    public int getIdKlienta() {
        return idKlienta;
    }

    public void setIdKlienta(int idKlienta) {
        this.idKlienta = idKlienta;
    }

    public String getLoginKlienta() {
        return loginKlienta;
    }

    public void setLoginKlienta(String loginKlienta) {
        this.loginKlienta = loginKlienta;
    }

    public String getPasswordKlienta() {
        return passwordKlienta;
    }

    public void setPasswordKlienta(String passwordKlienta) {
        this.passwordKlienta = passwordKlienta;
    }

    public String getNickKlienta() {
        return nickKlienta;
    }

    public void setNickKlienta(String nickKlienta) {
        this.nickKlienta = nickKlienta;
    }
}
