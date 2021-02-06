package client;

import server.Message;

import java.io.IOException;

public interface ServerService {

    boolean isConnected();
    String authorization(String login, String password) throws IOException;
    void closeConnection();

    void sendMessage(String message);
    Message readMessages();

}
