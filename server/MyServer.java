package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    public static final int PORT = 8081;//это порт будет слушать сервер

    private List<ClientHandler> clients;//здесь будем хранить список Клиентов - активных ??? Да, активных - это разделяемый ресурс между потоками
    private AuthService authService;

    public MyServer() throws InterruptedException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Ожидаем подключение клиентов.");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
//                System.out.println(clients);
//                System.out.println(clients.toString());
//                clients.add(new ClientHandler(this, socket));//добавляем клиента в список Клиентов, после его подключения
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    //Метод, который отправляет список подключённых клиентов всем активным клиентам
    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("Все подключённые клиенты: ");
        for (ClientHandler client : clients) {//идём циклом по всем Клиентам
            sb.append(client.getNick()).append(" ");
        }
        Message message = new Message();
        message.setMessage(sb.toString());
        try {
            if (clients.size() != 0)
                broadcastMessage(message);//Если клиентов НОЛЬ, то и не надо запускать broadcastMessage
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Напишем метод отправки сообщения конкретному пользователю
    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler client : clients) {//идём циклом по всем Клиентам
            if (client.getNick().equals(nickTo)) {
                System.out.printf("Отправляем личное сообщение от %s, кому %s", from.getNick(), nickTo);
                Message message = new Message();
                message.setNick(from.getNick());
                message.setMessage(msg);
                client.sendMessage(message);
                return;
            }
        }
        System.out.printf("Клиент с ником %s не подключен к чату.", nickTo);
        Message message = new Message();
        message.setMessage("Клиент с ником " + nickTo + " не подключен к чату.");
        from.sendMessage(message);
    }

    //Вернёмся к старому написанию метода отправки сообщений всем пользователям сразу
    public synchronized void broadcastMessage(Message message) throws IOException {//Отправка сообщения всем доступным Клиентам
        for (ClientHandler client : clients) {//идём циклом по всем Клиентам
            client.sendMessage(message);//отправка сообщения Клиенту
        }
    }

/*    public synchronized void broadcastMessage(Message message) throws IOException {//Отправка сообщения всем доступным Клиентам
//Реализовываем личные сообщения так: если клиент пишет «/w nick3 Привет», то только клиенту с ником nick3 должно прийти сообщение «Привет».
//        System.out.println(message + "АГА");
        if (message.getMessage().contains("/w")) {//проверяем вхождение /w
//            System.out.println("АГА, есть /w");
            String[] words = message.getMessage().split(" ");//Нарезаем сообщения на части, которые разделены пробелом, нас затем интересует второе слово words[1], которое должно содержать Ник Клиента
//            System.out.println("Выделяем Ник: " + words[1]);
//Возможно это не лучшее решение и надо в классах писать геттеры и сеттеры, чтобы получить List активных клиентов и работать потом с этим List, но такое решение работает
//Также делаем допущение, что сообщение НЕ отправляется самому клиенту, что как-бы не логично, это можно легко дописать, если надо
//Также Клиент может отправить сообщение самому себе, указав после /w свой Ник и тогда другие активные клиенты не увидят его сообщение
//Также не стал вырезать перед отправкой саму фразу /w nick3, это можно легко дописать, если надо
            for (ClientHandler client : clients) {//идём циклом по всем Клиентам
//            System.out.println(client.getNick());
                if (client.getNick().equals(words[1])) {//Если в списке пользователей есть Клиент, то отправляем только ему, если нет, то отправляем сообщение всем
//                    System.out.println("АГА, есть такой Клиент");
                    client.sendMessage(message);//отправка сообщения Клиенту
                    return;
                }
            }
        }
        for (ClientHandler client : clients) {//идём циклом по всем Клиентам
//            System.out.println(client.getNick());
            client.sendMessage(message);//отправка сообщения Клиенту
        }
    }*/

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (nick.equals(client.getNick())) {
                return true;
            }
        }
        return false;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        int x = clients.size();
        clients.add(clientHandler);
        if (x != clients.size()) {
            broadcastClientsList();//когда кто-то подключается, отправляем всем пользователям список активных клиентов}
        }
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        int x = clients.size();
        clients.remove(clientHandler);
        if (x != clients.size()) {
            broadcastClientsList();//когда кто-то отключается, отправляем всем пользователям список активных клиентов}
        }
    }
}