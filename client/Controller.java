package client;

//При сохранении из SceneBuilder он портит код client.fxml и надо править его, добавив  fx:controller="client.Controller", чтобы всё работало
//fx:controller="client.Controller"
//Зачем-то я обновил SceneBuilder до 15-ой версии и теперь Идея на это ругается при запуске программы. Чтобы Идея не ругалась ещё надо в client.fxml код xmlns="http://javafx.com/javafx/15.0.1" исправлять на xmlns="http://javafx.com/javafx/11.0.1"

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.ClientHandler;
import server.Message;
import javafx.collections.*;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Controller implements Initializable {

    private ServerService serverService;

    @Override
    //Всегда отрабатывает при старте программы
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        System.out.println("Всегда отрабатывает при старте программы");
        loginTextField.requestFocus();//передаёт фокус в поле ввода Логина
        mainChat.setEditable(false);//не даём ничего вводить в окошко вывода сообщений чата
//        loginTextField.setToolTipText("Введите здесь логин");
/*        ObservableList<String> langs = FXCollections.observableArrayList("Ivan", "JavaScript", "C#");
        ComboBox<String> loginComboBox = new ComboBox<String>(langs);
        loginComboBox.setValue("Ivan"); // устанавливаем выбранный элемент по умолчанию*/
//        loginComboBox.getItems().add("Ivan");//добавляем строки в loginComboBox
        // можно по одному добавлять, а можно сразу несколько добавить
        authLabel.setText("Вы офф-лайн.");
        loginComboBox.getItems().setAll(
                "ivan",
                "sharik",
                "petr"
        );
//        loginComboBox.setValue("ivan"); // устанавливаем выбранный элемент по умолчанию
    }

/*    @FXML
//Всегда отрабатывает при старте программы
    public void initialize() {
//        System.out.println("Привет!!!");
        loginTextField.requestFocus();//передаёт фокус в поле ввода Логина
        mainChat.setEditable(false);//не даём ничего вводить в окошко вывода сообщений чата
//        loginTextField.setToolTipText("Введите здесь логин");
*//*        ObservableList<String> langs = FXCollections.observableArrayList("Ivan", "JavaScript", "C#");
        ComboBox<String> loginComboBox = new ComboBox<String>(langs);
        loginComboBox.setValue("Ivan"); // устанавливаем выбранный элемент по умолчанию*//*
        loginComboBox.getItems().add("Осень");
    }*/

/*    @FXML
//Всегда срабатывает при закрытии приложения - это не срабатывает, прописал обработку нажатия на крестик в ClientApp - primaryStage.setOnCloseRequest
    public void stop() {
        System.out.println("Приложение закрывается!!!");
        //Здесь Вы можете прописать все действия при закрытии Вашего приложения.
    }*/

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private TextArea mainChat;

    @FXML
    private TextArea myMessage;

    @FXML
    private Button sendMessageButton;

    @FXML
//    private ComboBox<?> loginComboBox;//В таком виде нам даёт контроллер Scene Builder
    private ComboBox<String> loginComboBox;//Указываем явно в треугольных скобках что у нас будут список со строками

    @FXML
    private Label authLabel;

    //Выбор в верхнем меню Файл-->Выход Ctrl+Q
    @FXML
    void doExit(ActionEvent event) {
//        ClientApp.stop();
        Thread.interrupted();//Останавливаем поток
        System.exit(0);//Выходим из программы
    }

    //Нажатие кнопки Войти в ИванЧат
    @FXML
    void doLogin(ActionEvent event) {
        //Надо сразу проверить, есть-ли уже подключение и если есть, то выдаём сообщение об ошибке и дальше не идём
        if (SocketServerService.isConnected) {
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы уже авторизованы.\nМожете пользоваться чатом.");
            return;
        }
        if (loginTextField.getText().trim() == "") {//trim здесь потому, что не может быть логина, состоящего из пробелов
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели ни какой логин.\nВведите логин и попробуйте ещё разик.");
            loginTextField.setText("");//Очищаем поле ввода логина
            loginTextField.requestFocus();//передаёт фокус в поле ввода Логина
            return;
        }
        if (passwordPasswordField.getText().trim() == "") {//trim здесь потому, что не может быть пароля, состоящего из пробелов
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели ни какой пароль.\nВведите пароль и попробуйте ещё разик.");
            passwordPasswordField.setText("");//Очищаем поле ввода пароля
            passwordPasswordField.requestFocus();//передаёт фокус в поле ввода Пароля
            return;
        }
/*        if (!serverService.isConnected()){
            mainChat.appendText("\nСервер\nПопробуйте авторизоваться позже.");
        }*/

        //Авторизуемся и запускаем потом, который будет получать сообщения от сервера здесь, в Нажатии кнопки Войти в ИванЧат, нет смысла делать это заранее в другом месте программы
/*        SocketServerService.login = loginTextField.getText();
        SocketServerService.password = passwordPasswordField.getText();*/
//        System.out.println("1 " + SocketServerService.messageServerErrors);
        serverService = new SocketServerService();
        authLabel.setText("Вы офф-лайн.");
        String nick = "";
        try {
            nick = serverService.authorization(loginTextField.getText(), passwordPasswordField.getText());//Авторизуемся, login и пароль занесли в вызов метода serverService.authorization
            if (nick != "") {
                authLabel.setText("Вы он-лайн, Ваш Ник: " + nick);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //пробрасывание исключения не помогло передать информацию о том, что сервер не доступен. Что-то я не так делаю
        }
//        System.out.println("2 " + SocketServerService.messageServerErrors + nick + SocketServerService.isConnected+" "+ ClientHandler.messageErrorLoginPassword);
//        System.out.println("Не удачная попытка авторизации на стороне клиента");
        //Обрабатываем ошибку подключения к серверу, когда сервер не доступен
        if (SocketServerService.messageServerErrors == "Сервер не доступен.") {//да, это 100% не лучший метод, но я не умею пока по-другому даже equals не стал использовать
            mainChat.appendText("\n" + SocketServerService.messageServerErrors + "\nПопробуйте авторизоваться позже.");
            return;
        }
/*        if (nick == "Такого Ника не может быть никогда") {//мне это очень не нравится, но я уже ковыряю код часа четыре и принял решение заколхозить
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы ввели не верный логин или пароль.\nВведите логин/пароль и попробуйте ещё разик.");
            loginTextField.requestFocus();//передаёт фокус в поле ввода Логина
            return;
        }*/
        if (SocketServerService.isConnected) {//Если успешно авторизовались, то выполняем этот код
            new Thread(() -> {//поток, который висит, получает сообщения от сервера и выводит их в окно чата
                while (true) {
                    printToUI(mainChat, serverService.readMessages());
                }
            }).start();
            mainChat.setText("Подключение успешно выполнено.");
            serverService.sendMessage("Всем привет. Я вошёл в чат.");
//            Message message = new Message();
//            message.getNick();
//            mainChat.appendText(message.getNick());
            //как-то надо получить Ник пользователя
//            server.BaseAuthService
//            getNickByLoginAndPass
            return;
        } else {
//            System.out.println("Не удачная попытка авторизации на стороне клиента");
            return;
        }
    }

    @FXML
        //Этот метод будет вызываться каждый раз, когда мы будем переключать элемент списка
    void doLoginPasswordPodstavit(ActionEvent event) {
        loginTextField.setText(loginComboBox.getSelectionModel().getSelectedItem());
        passwordPasswordField.setText("password");
    }

/*    //Нажатие кнопки подставить логин/пароль - это для теста, потом надо удалить
//Оставлю, чтобы знать, как я это делал, чтобы пользоваться в будущем как подсказкой
    @FXML
    void doEnterLoginPasswordButton(ActionEvent event) {
        loginTextField.setText(testLoginLabel.getText());
        passwordPasswordField.setText(testPasswordLabel.getText());
    }

    //Нажатие кнопки подставить логин/пароль - это для теста, потом надо удалить
    @FXML
    void doEnterLoginPasswordButton2(ActionEvent event) {
        loginTextField.setText(testLoginLabel2.getText());
        passwordPasswordField.setText(testPasswordLabel2.getText());
    }

    //Нажатие кнопки подставить логин/пароль - это для теста, потом надо удалить
    @FXML
    void doEnterLoginPasswordButton3(ActionEvent event) {
        loginTextField.setText(testLoginLabel3.getText());
        passwordPasswordField.setText(testPasswordLabel3.getText());
    }*/

    //Выбор в верхнем меню Помощь-->О программе
    //Выводит подсказку к программе
    @FXML
    void doAboutProgramm() {
        showMessage(Alert.AlertType.INFORMATION, "О программе", "---===ИванЧат от Иван Булкин продакшн===---\n                        Copyright © 2021");
    }

    //добавляет сообщение в окно чата
    private void printToUI(TextArea mainChat, Message message) {
//        mainChat.appendText("\n" + message.getNick() + ": " + message.getMessage());//Добавляем с новой строки
        mainChat.appendText(message.getNick() != null ? "\n" + message.getNick() + ": " + message.getMessage() : "\nСервер: " + message.getMessage());//Добавляем с новой строки
    }

    //Нажатие кнопки Отправить сообщение
    @FXML
    void doSendMessageButton(ActionEvent event) {
/*        Thread thread1 = new Thread() {
            public void run() {*/
        if (!SocketServerService.isConnected) {//Не даём отправлять сообщения и выдаём сообщение об ошибке, если Клиент не авторизован
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не авторизованы и не можете отправлять сообщения.\nАвторизуйтесь, чтобы начать пользоваться чатом.");
            return;
        }
        if (myMessage.getText() == "") {//Если мы ничего не ввели, то не отправляем сообщения
            return;
        }
//        scheduler();
        serverService.sendMessage(myMessage.getText());
        myMessage.clear();//Очищаем окно ввода сообщений
        myMessage.requestFocus();//Возвращаем фокус в окно ввода сообщений
//        System.out.println("Почему это сразу не отрабатывает???");
/*            }
        };
        Thread thread2 = new Thread() {
            @Override
            //переопределяем метод run
            public void run() {
                System.out.println("Срабатывает таймер.");
            }
        };
        try {
            thread2.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread1.start();
        thread2.start();*/
/*        try {
            thread2.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
/*        new Thread(() -> {//создаём поток, который висит 120 секунд
            int seconds = 0;
            while (true) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Timer timer = new Timer();
                timer.schedule(null, 5000);
                if (seconds == 0) {
                    System.out.println("Срабатывает таймер.");

                }
            }
        }).start();*/
    }


    /*    private void scheduler() {
     *//*        Timer time = new Timer();
        time.schedule(null, 1000); // Создаем задачу с повторением через 1 сек.*//*
        Thread thread2 = new Thread() {
            @Override
            //переопределяем метод run
            public void run() {
                System.out.println("Срабатывает таймер.");
            }
        };
        try {
            thread2.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
    }*/

/*    //отправляем сообщение
//перенёс это в нажатие кнопки Отправить сообщение
    private void sendMessage(TextArea myMessage) {
        serverService.sendMessage(myMessage.getText());
    }*/

    //Вот этот метод сделала сама Идея, когда я нажал на блоке кода Ctrl+Alt+M круто, в принципе
    private void showMessage(Alert.AlertType error, String s, String s2) {
        var alert = new Alert(error);
        alert.setTitle(s);
        alert.setResizable(false);
        alert.setHeaderText(s2);
        alert.show();
    }

}