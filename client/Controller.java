package client;

//При сохранении из SceneBuilder он портит код client.fxml и надо править его, добавив  fx:controller="client.Controller", чтобы всё работало
//fx:controller="client.Controller"
//Зачем-то я обновил SceneBuilder до 15-ой версии и теперь Идея на это ругается при запуске программы. Чтобы Идея не ругалась ещё надо в client.fxml код xmlns="http://javafx.com/javafx/15.0.1" исправлять на xmlns="http://javafx.com/javafx/11.0.1"

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import server.Message;

import java.io.IOException;

public class Controller {

    private ServerService serverService;

    @FXML
//Всегда отрабатывает при старте программы
    public void initialize() {
//        System.out.println("Привет!!!");
        loginTextField.requestFocus();//передаёт фокус в поле ввода Логина
        mainChat.setEditable(false);//не даём ничего вводить в окошко вывода сообщений чата
    }

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
    private Button testEnterLoginPasswordButton;//это для теста, потом надо удалить

    @FXML
    private Button testEnterLoginPasswordButton2;//это для теста, потом надо удалить

    @FXML
    private Button testEnterLoginPasswordButton3;//это для теста, потом надо удалить

    @FXML
    private Label testLoginLabel;//это для теста, потом надо удалить

    @FXML
    private Label testLoginLabel2;//это для теста, потом надо удалить


    @FXML
    private Label testLoginLabel3;//это для теста, потом надо удалить

    @FXML
    private Label testPasswordLabel;//это для теста, потом надо удалить

    @FXML
    private Label testPasswordLabel2;//это для теста, потом надо удалить

    @FXML
    private Label testPasswordLabel3;//это для теста, потом надо удалить

    //Выбор в верхнем меню Файл-->Выход Ctrl+Q
    @FXML
    void doExit(ActionEvent event) {
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
        //Авторизуемся и запускаем потом, который будет получать сообщения от сервера здесь, в Нажатии кнопки Войти в ИванЧат, нет смысла делать это заранее в другом месте программы
        SocketServerService.login = loginTextField.getText();
        SocketServerService.password = passwordPasswordField.getText();
        serverService = new SocketServerService();
        try {
            serverService.openConnection();//Авторизуемся
        } catch (IOException e) {
//            e.printStackTrace();
            //пробрасывание исключения не помогло передать информацию о том, что сервер не доступен. Что-то я не так делаю
        }
        //Обрабатываем ошибку подключения к серверу, когда сервер не доступен
        if (SocketServerService.messageServerErrors == "Сервер не доступен.") {//да, это 100% не лучший метод, но я не умею пока по-другому даже equals не стал использовать
            mainChat.appendText("\n" + SocketServerService.messageServerErrors + "\nПопробуйте авторизоваться позже.");
            return;
        }
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
        } else {
            return;
        }
    }

    //Нажатие кнопки подставить логин/пароль - это для теста, потом надо удалить
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
    }

    //Выбор в верхнем меню Помощь-->О программе
    //Выводит подсказку к программе
    @FXML
    void doAboutProgramm() {
        showMessage(Alert.AlertType.INFORMATION, "О программе", "---===ИванЧат от Иван Булкин продакшн===---\n                        Copyright © 2021");
    }

    //добавляет сообщение в окно чата
    private void printToUI(TextArea mainChat, Message message) {
        mainChat.appendText("\n" + message.getNick() + ": " + message.getMessage());//Добавляем с новой строки
    }

    //Нажатие кнопки Отправить сообщение
    @FXML
    void doSendMessageButton(ActionEvent event) {
        if (!SocketServerService.isConnected) {//Не даём отправлять сообщения и выдаём сообщение об ошибке, если Клиент не авторизован
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не авторизованы и не можете отправлять сообщения.\nАвторизуйтесь, чтобы начать пользоваться чатом.");
            return;
        }
        if (myMessage.getText() == "") {//Если мы ничего не ввели, то не отправляем сообщения
            return;
        }
        serverService.sendMessage(myMessage.getText());
        myMessage.clear();//Очищаем окно ввода сообщений
        myMessage.requestFocus();//Возвращаем фокус в окно ввода сообщений
    }

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