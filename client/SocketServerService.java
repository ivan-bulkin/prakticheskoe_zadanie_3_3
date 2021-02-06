package client;

import com.google.gson.Gson;
import com.sun.javafx.scene.shape.MeshHelper;
import server.AuthMessage;
import server.Message;
import server.MyServer;

import java.io.*;
import java.net.Socket;

public class SocketServerService implements ServerService {

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    public static boolean isConnected = false;//переменная, которая отвечает за то, что подсоединились мы к серверу или нет
    public static String messageServerErrors = "";//переменная, через которую будем передавать различные ошибки - потом решил сделать по-другому: пробросил исключение через openConnection
    //пробрасывание исключения не помогло передать информацию о том, что сервер не доступен. Что-то я не так делаю, поэтому возвращаемся к передачи информации о том, что сервер не доступен через переменную messageServerErrors
/*    public static String login;//Значение Логина будем заполнять из поля формы
    public static String password;//Значение Пароля будем заполнять из поля формы*/

    public boolean isConnected() {
        return isConnected;
    }

    //    @Override
    public String authorization(String login, String password) throws IOException {//openConnection заменили на authorization
        try {
            messageServerErrors = "Сервер не доступен.";//присваиваем значение "Сервер не доступен.", чтобы потом отработать ошибку, если сервер не доступен
            isConnected = false;//На всякий случай тоже очистим, а точнее: присвоим false
            socket = new Socket("localhost", MyServer.PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            AuthMessage authMessage = new AuthMessage();
            authMessage.setLogin(login);
            authMessage.setPassword(password);
            dataOutputStream.writeUTF(new Gson().toJson(authMessage));//Авторизуемся на сервере

            authMessage = new Gson().fromJson(dataInputStream.readUTF(), AuthMessage.class);
            if (authMessage.isAuthentificated()) {
                isConnected = true;
            }
            messageServerErrors = "";//Успешно авторизовались и переменная очищена
            return authMessage.getNick();
        } catch (IOException e) {//(IOException ignored) - так можно написать, чтобы игнорировать исключения IOException
//            System.out.println("3 " + messageServerErrors);
//            messageServerErrors = "Сервер не доступен.";//пробрасывание исключения не помогло передать информацию о том, что сервер не доступен. Что-то я не так делаю, поэтому возвращаемся к передачи информации о том, что сервер не доступен через переменную messageServerErrors
//            System.out.println("Не удачная попытка авторизации на стороне клиента");
//            e.printStackTrace();//заккоментируем это, чтобы не видеть на экране кучу нехороших ругательств от Java, когда что-то пошло не так при подключении
        }
//        System.out.println("Не удачная попытка авторизации на стороне клиента");
        return "";
//        System.out.println("Тут мы "+server.AuthMessage.getNick());
    }

    @Override
    public void closeConnection() {//Закрываем соединение
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);

        try {
            dataOutputStream.writeUTF(new Gson().toJson(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readMessages() {
        try {
            return new Gson().fromJson(dataInputStream.readUTF(), Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Message();//возвращаем пустую строку и ошибка, что ниже уходит
        }
    }//тут была ошибка при самом первом запуске программы время записи в лекции № 7 1:48:52
}
