package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private MyServer myServer;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public String nick;

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {//таким кодом мы переоопределяем метод run, можно написать как написано, см. ниже или ещё вариант см. ещё ниже. Этот поток будет запущен и постоянно принимать сообщения от Клиентов
                try {
                    authentification();//Аутентификация Клиента
                    readMessages();//Чтение сообщения от Клиента
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
/*            new Thread(new Runnable() {
                @Override
                public void run() {
                    ClientHandler.this.readMessages();
                }
            }).start();*/
//            new Thread(this::readMessages).start();//а можно и так :-)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        Message message = new Message();
        message.setMessage(nick + " вышел из чата");
        try {
            myServer.broadcastMessage(message);
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentification() {//Аутентификация Клиента
        while (true) {
            try {
                AuthMessage message = new Gson().fromJson(dataInputStream.readUTF(), AuthMessage.class);
                String nick = myServer.getAuthService().getNickByLoginAndPass(message.getLogin(), message.getPassword());
                if (nick != null && !myServer.isNickBusy(nick)) {
                    message.setAuthentificated(true);
                    dataOutputStream.writeUTF(new Gson().toJson(message));
                    Message broadcastMsg = new Message();
                    broadcastMsg.setMessage(nick + " вошёл в чат.");//здесь идёт сообщение от null - это сервер, надо-бы его для красоты заменить на слово Сервер
                    myServer.broadcastMessage(broadcastMsg);//отправляем сообщением всем Клиентам
                    myServer.subscribe(this);
                    this.nick = nick;
                    return;//Не работало, из-за того, что не было этой директивы и мы не выходили из цикла. Запись урока 7 1:53:40
                } else {
                    //здесь-бы надо написать что-то, что отвечает за количество попыток авторизации, чтобы при авторизации нельзя было бесконечно долго подбирать логины/пароли
                }
//не стали использовать вариант, что ниже. Создали класс AuthMessage
/*                Message message = new Gson().fromJson(dataInputStream.readUTF(), Message.class);
                if (!message.getMessage().startsWith("/auth")) {//Если сообщение не начинается на /auth, то переходим дальше
                    continue;
                }
                String[] parts = message.getMessage().split("\\s");// \\s - означает, что мы будем разделять сторку ровно по одному пробельному символу
                // /auth login password
                if (parts.length != 3) {
                    continue;
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessages() throws IOException {//Чтение сообщения от Клиента
        while (true) {//бесконечный цикл
            Message message = new Gson().fromJson(dataInputStream.readUTF(), Message.class);//Получаем сообщение от Клиента
            message.setNick(nick);//сам Клиент не предаёт свой Ник, Ник Клиента находится в списке Логин/Пароль/Ник
            System.out.println(message);//Сервер печатает передаваемые собщения в консоль, можно заккоментировать
            if ("/end".equals(message.getMessage())) {
                return;
            }
            myServer.broadcastMessage(message);//Отправка сообщения всем доступным Клиентам
        }
    }

    public void sendMessage(Message message) {//отправка сообщения Клиенту
        try {
            dataOutputStream.writeUTF(new Gson().toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}

