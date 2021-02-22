package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private Socket socket;
    private MyServer myServer;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public String nick;
    public String id;

/*    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {//таким кодом мы переоопределяем метод run, можно написать как написано, см. ниже или ещё вариант см. ещё ниже. Этот поток будет запущен и постоянно принимать сообщения от Клиентов
                try {
                    authentification();//Аутентификация Клиента
                    //Если введён не правильный логин/пароль, то мы всё равно попадаем сюда и программа клиента наглухо зависает, при этом соединение всё-таки происходит и Сервер пишет: Клиент подключился
//                    closeConnection();
//                    System.out.println("Отловим здесь");
                    readMessages();//Чтение сообщения от Клиента
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
*//*            new Thread(new Runnable() {
                @Override
                public void run() {
                    ClientHandler.this.readMessages();
                }
            }).start();*//*
//            new Thread(this::readMessages).start();//а можно и так :-)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    //попробуем всё это переписать
    public ClientHandler(MyServer myServer, Socket socket) throws InterruptedException {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    authentification();//Аутентификация Клиента
                    try {
                        readMessages();//Чтение сообщения от Клиента
                    } catch (IOException e) {
                        if (e.getMessage().equals("Connection reset"))
                            System.out.println("Клиент: " + nick + " Сбросил соединение с сервером.");
                        if (e.getMessage().equals("Socket closed"))
                            System.out.println("Клиент: " + nick + " Не смог подключиться к серверу.");
//                        e.printStackTrace();
                    } finally {
                        closeConnection();//закрываем соединение с клиентом
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(10000);//Это 10 секунд, если надо 120 секунд, то надо написать 120000 Я помню, на каком-то уроке мы внутри sleep() писали какое-то красивое преобразование секунд в миллисекунды, то с наскоку не нашёл, где это и интернет мне не помог, поэтому делаем в лоб
        if (nick == null) {
            closeConnection();//Закрываем соединение, если пользователь ввёл не верный логин или пароль, а значит не авторизовался
        }
    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        Message message = new Message();
        message.setMessage(nick + " вышел из чата");
        try {
            if (nick != null) myServer.broadcastMessage(message);
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
                String id = myServer.getAuthService().getIdByLoginAndPass(message.getLogin(), message.getPassword());
                if (nick != null && !myServer.isNickBusy(nick)) {
                    message.setAuthentificated(true);
                    message.setNick(nick);
                    message.setId(id);
                    this.nick = nick;
                    this.id = id;
                    dataOutputStream.writeUTF(new Gson().toJson(message));
                    Message broadcastMsg = new Message();
                    broadcastMsg.setNick(nick);
                    broadcastMsg.setId(id);
                    //заккоментируем эти строчки и сообщение о том, что кто-то вошёл в чат перенесём в нажатие кнопки Войти в чат
                    broadcastMsg.setMessage(nick + " вошёл в чат." + ", Id: " + id);//здесь идёт сообщение от null - это сервер, надо-бы его для красоты заменить на слово Сервер
                    myServer.broadcastMessage(broadcastMsg);//отправляем сообщением всем Клиентам
                    myServer.subscribe(this);
                    this.nick = nick;
                    this.id = id;
                    return;//Не работало, из-за того, что не было этой директивы и мы не выходили из цикла. Запись урока 7 1:53:40
                } else {
                    System.out.println("Не удачная попытка авторизации на стороне сервера, Ник: " + nick);
                    return;
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
            //блок кода, который выполняется, когда изменяется Ник пользователя
            if (message.getMessage().contains("я сменил свой Ник, мой новый Ник")) {
                String newnick = message.getMessage();
                newnick = newnick.replace("я сменил свой Ник, мой новый Ник: ", "");//в newnick остаётся новый Ник
                System.out.println("Сервер: Пользователь " + nick + " сменил свой Ник, его новый Ник: " + newnick);
//здесь надо написать блок кода, который будет изменять Ник клиента на новый, чтобы все новые на данный момент сообщения отправлялись уже от нового Ника
                nick = newnick;
//                System.out.println(BaseAuthService.entries.size());
//                System.out.println(BaseAuthService.entries.subList(0,2).toString());
//                String id = myServer.getAuthService().getIdByLoginAndPass("3", "3");
//                String s = getIdByLoginAndPass("","");
//                System.out.println(id);
            }
            if (!message.getMessage().startsWith("/")) {//Если сообщение не начинается на /, то отправляем сообщение всем
                myServer.broadcastMessage(message);//Отправка сообщения всем доступным Клиентам
                continue;
            }
            // / <command> <message> - сообщения с системными командами начинаются на /
            String[] tokens = message.getMessage().split("\\s");
            switch (tokens[0]) {
                case "/end": {
                    return;
                }
                case "/w": {// /w <nick> <message> - отправляем сообщение только одному конкретному пользователю
                    if (tokens.length < 3) {
                        Message msg = new Message();
                        msg.setMessage("Не хватает параметров, необходимо отправить команду следующего вида: /w <ник> <сообщение>");
                        this.sendMessage(msg);
                        break;//Необходимо было здесь это дописать, иначе отрабатывало дальше и программа зависала
                    }
                    String nick = tokens[1];
                    String msg = tokens[2];
                    myServer.sendMsgToClient(this, nick, msg);
                    break;
                }
            }
/*            if ("/end".equals(message.getMessage())) {
                return;*/
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

