package client;

import server.Message;

import java.io.IOException;

public interface ServerService {

    void openConnection() throws IOException;
    void closeConnection();

    void sendMessage(String message);
    Message readMessages();

}
