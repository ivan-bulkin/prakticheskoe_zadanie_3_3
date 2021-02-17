package server;

//это про авторизацию Клиентов
public interface AuthService {
    void start();

    void stop();

    String getNickByLoginAndPass(String login, String password);

    String getIdByLoginAndPass(String login, String password);

//    String getIdByLoginAndPass(String login, String password);

//    String getIdByLoginAndPass(String login, String password);

//    String getIdByLoginAndPass(String login, String password);

//    String getIdByLoginAndPass(String login, String password);
}
