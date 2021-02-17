package client;

//При сохранении из SceneBuilder он портит код client.fxml и надо править его, добавив  fx:controller="client.Controller", чтобы всё работало
//fx:controller="client.Controller"
//Зачем-то я обновил SceneBuilder до 15-ой версии и теперь Идея на это ругается при запуске программы. Чтобы Идея не ругалась ещё надо в client.fxml код xmlns="http://javafx.com/javafx/15.0.1" исправлять на xmlns="http://javafx.com/javafx/11.0.1"

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import server.AuthMessage;
import server.BaseAuthService;
import server.Message;
import javafx.collections.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class Controller implements Initializable {

    private ServerService serverService;
    public static Connection conn = null;//будем использовать одно соединение на всю программу
    public static Statement statement;//будем использовать одно соединение на всю программу
    private ObservableList<User> usersData = FXCollections.observableArrayList();//для работы с таблицой users, в т.ч. для заполения tableview

    //    public static String idKlienta;//сюда положим id клиента из users.id_klienta и будем его дальше использовать
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
        //загружаем драйвер для работы с БД
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Драйвер базы данных MySQL успешно загружен!");
        } catch (Exception ex) {
            System.out.println("Драйвер MySQL НЕ загрузился.");
            System.out.println(ex);
        }
        //подключаемся к базе данных и будем использовать одно соединение на всю программу
        try {
            conn = DriverManager.getConnection("jdbc:mysql://89.108.72.116:3306/ivanchat", "ivan", "ruaFZ7rw4XSohkR1");
            conn.setAutoCommit(true);
            statement = conn.createStatement();
            System.out.println("Соединение с базой данных успешно произведено!");
        } catch (Exception ex) {
            System.out.println("Соединение с базой данных не произведено.");
            System.out.println(ex);
        }
        try {
            showUsersTable();//Показываем таблицу с пользователями
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //хотел вроде как закрыть соединени с БД при выходе из программы, но это блок отрабатывает сразу же при старте программы. Не стал пока разбираться в чём дело
        /* finally {
            if (conn != null) {
                try {
                    System.out.println("Сейчас закроем соединение с базой данных.");
                    conn.close();
                    System.out.println("Соединение с базой данных успешно закрыто!");
                } catch (Exception ex) {
                    System.out.println("Хрен. Почему-то не закрылось соединение с базой данных.");
                }
            }
        }*/
        //устанавливаем тип и значение которое должно хранится в колонках таблицы с пользователями
        idKlientaColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("idKlienta"));
        loginKlientaColumn.setCellValueFactory(new PropertyValueFactory<User, String>("loginKlienta"));
        passwordKlientaColumn.setCellValueFactory(new PropertyValueFactory<User, String>("passwordKlienta"));
        nickKlientaColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nickKlienta"));
/*        //это заполняет loginComboBox
        loginComboBox.getItems().setAll(
                "ivan",
                "sharik",
                "petr"
        );*/
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

//    @FXML
//    private ComboBox<?> loginComboBox;//В таком виде нам даёт контроллер Scene Builder
//    private ComboBox<String> loginComboBox;//Указываем явно в треугольных скобках что у нас будут список со строками

    @FXML
    private Label authLabel;

    @FXML//Кнопка Создать таблицу Пользователей
    private Button createTableUsersButton;

    @FXML
    private TextField loginTextField1;

    @FXML
    private TextField loginTextField11;

    @FXML
    private PasswordField passwordPasswordField1;

    @FXML
    private PasswordField passwordPasswordField2;

    @FXML//Кнопка Создать пользователя
    private Button createUserButton;

    @FXML//Таблица с пользователями
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> idKlientaColumn;

    @FXML
    private TableColumn<User, String> loginKlientaColumn;

    @FXML
    private TableColumn<User, String> passwordKlientaColumn;

    @FXML
    private TableColumn<User, String> nickKlientaColumn;

    //Выбор в верхнем меню Файл-->Выход Ctrl+Q
    @FXML
    void doExit(ActionEvent event) {
//        ClientApp.stop();
        Thread.interrupted();//Останавливаем поток
        System.exit(0);//Выходим из программы
    }

    @FXML//Поле ввода нового Ника
    private TextField newNikTextField;

    @FXML
//Нажатие кнопки <-- сменить Ник
    void doChangeNik(ActionEvent event) {
        System.out.println("Нажатие кнопки <-- сменить Ник");
        if (!SocketServerService.isConnected) {//Не даём изменять Ник и выдаём сообщение об ошибке, если Клиент не авторизован
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не авторизованы и не можете изменить свой Ник.\nАвторизуйтесь, чтобы начать пользоваться чатом.");
            return;
        }
        try {
            //изменяем Ник пользователя
            int x = statement.executeUpdate("update users set nick_klienta='" + newNikTextField.getText() + "' where id_klienta=" + SocketServerService.idKlienta);
            if (x == 1) {
                SocketServerService.nickKlienta = newNikTextField.getText();
                showMessage(Alert.AlertType.INFORMATION, "УСПЕШНО", "Ваш Ник изменён. Ваш новый Ник: " + SocketServerService.nickKlienta);
                authLabel.setText("Вы он-лайн, Ваш Ник: " + SocketServerService.nickKlienta + ", Ваш id: " + SocketServerService.idKlienta);
                showUsersTable();//обновим таблицу со всеми пользователями
//здесь надо дописать, чтобы Ник обновился, иначе Ник в таблице изменён, но сообщения продолжают приходит под старым Ником
                return;
            } else {
                showMessage(Alert.AlertType.ERROR, "Ошибка", "Что-то пошло не так, попробуйте ещё разик.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
//Нажатие кнопки Создать пользователя
    void doCreateUser(ActionEvent event) {
        System.out.println("Нажатие кнопки Создать пользователя");
        //проверяем, что таблица users создана
        try {
            statement.executeQuery("select id_klienta from users order by id_klienta");
        } catch (SQLException throwables) {
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Таблицы Пользователей пока не существует.\nСперва Вы должны создать таблицу Пользователей.\nДля этого нажмите кнопку Создать таблицу Пользователей.");
            return;
        }
        if (loginTextField1.getText().trim() == "") {//trim здесь потому, что не может быть логина, состоящего из пробелов
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели ни какой логин.\nВведите логин и попробуйте ещё разик.");
//            loginTextField1.setText("");//Очищаем поле ввода логина
            loginTextField1.requestFocus();//передаёт фокус в поле ввода Логина
            return;
        }
        if (loginTextField11.getText().trim() == "") {//trim здесь потому, что не может быть логина, состоящего из пробелов
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели ни какой Ник пользователя.\nВведите Ник пользователя и попробуйте ещё разик.");
//            loginTextField11.setText("");//Очищаем поле ввода Ника
            loginTextField11.requestFocus();//передаёт фокус в поле ввода Ника
            return;
        }
        if (passwordPasswordField1.getText().trim() == "") {//trim здесь потому, что не может быть логина, состоящего из пробелов
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели ни какой пароль пользователя.\nВведите пароль и попробуйте ещё разик.");
//            passwordPasswordField1.setText("");//Очищаем поле ввода пароля
            passwordPasswordField1.requestFocus();//передаёт фокус в поле ввода пароля
            return;
        }
        if (passwordPasswordField1.getText().trim().equals(passwordPasswordField2.getText().trim())) {//trim здесь потому, что не может быть логина, состоящего из пробелов
        } else {
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Вы не верно вводите Повтор пароля пользователя.\nВведите верно повтор пароля и попробуйте ещё разик.");
//            passwordPasswordField2.setText("");//Очищаем поле ввода повтора пароля
            passwordPasswordField2.requestFocus();//передаёт фокус в поле повтора пароля
            return;
        }
        try {/*(Connection conn = DriverManager.getConnection("jdbc:mysql://89.108.72.116:3306/ivanchat", "ivan", "ruaFZ7rw4XSohkR1")) {
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();*/
            //Проверяем, что пользователя с таким логином не существует и если существует, то не даём идти дальше
            //Пользователи с одинаковым Ником существовать могут
            //Можно также в MySQL таблице на всякий случай поле login_klienta сделать уникальным и обрабатывать тогда исключение, которое будет отдавать БД при попытке создать пользователей с одинаковым логином
            ResultSet resultSet = statement.executeQuery("select id_klienta from users where login_klienta='" + loginTextField1.getText() + "'");
            if (resultSet.next()) {
                showMessage(Alert.AlertType.ERROR, "Ошибка", "Пользователь с логином " + loginTextField1.getText() + " уже существует.\nВведите другой логин и попробуйте ещё разик.");
                loginTextField1.requestFocus();//передаёт фокус в поле ввода Логина
                return;
            }
            //Создаём пользователя
            int x = statement.executeUpdate("insert into users (login_klienta,password_klienta,nick_klienta,date_create_user) values ('" + loginTextField1.getText() + "','" + passwordPasswordField1.getText() + "','" + loginTextField11.getText() + "',now())");
            if (x == 1) {
                showMessage(Alert.AlertType.INFORMATION, "УСПЕШНО", "Пользователь с логином " + loginTextField1.getText() + " успешно создан");
                loginTextField1.setText("");//Очищаем поле ввода логина
                loginTextField11.setText("");//Очищаем поле ввода Ника
                passwordPasswordField1.setText("");//Очищаем поле ввода пароля
                passwordPasswordField2.setText("");//Очищаем поле ввода повтора пароля
                showUsersTable();
                return;
            } else {
                showMessage(Alert.AlertType.ERROR, "Ошибка", "Пользователь с логином " + loginTextField1.getText() + " НЕ создан.\nВведите другой логин и попробуйте ещё разик.");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //процедура заполнения данными usersData, в том числе заполняем TableView
    private void showUsersTable() throws SQLException {
        try {
            usersTableView.getItems().clear();//очищаем usersTableView, иначе записи всё будут добавляться и добавляться в usersTableView
            ResultSet resultSet;
            //                id_klienta.setCellValueFactory(new PropertyValueFactory<User, Integer>("№"));
//                usersData.add(new User(1, "Alex", "qwerty", "Alex"));
            resultSet = statement.executeQuery("select id_klienta, login_klienta, password_klienta, nick_klienta from users order by id_klienta");
//            resultSet.first();//устанавливаем на первую запись, почему-то каждый раз возникает исключение, если оставить эту запись
            while (resultSet.next()) {
                usersData.add(new User(resultSet.getInt("id_klienta"), resultSet.getString("login_klienta"), resultSet.getString("password_klienta"), resultSet.getString("nick_klienta")));
            }
            usersTableView.setItems(usersData);
//                usersTableView.
/*                resultSet = statement.executeQuery("select id_klienta, name_klienta from users order by id_klienta");
                while (resultSet.next()) {
                    StringBuilder builder = new StringBuilder();
//                    builder.append(resultSet.getString(1));//Указываем номер колонки
                    builder.append(resultSet.getString("id_klienta"));
                    builder.append(resultSet.getString("name_klienta"));//Указываем имя колонки
                    System.out.println(builder);
                }*/
        } catch (SQLException throwables) {
            System.out.println("Таблицы users пока не существует");
        }
    }

    @FXML
//Нажатие кнопки Создать таблицу Пользователей
    void doCreateTableUsers(ActionEvent event) throws SQLException {
//        boolean result;
        System.out.println("Нажатие кнопки Создать таблицу Пользователей");
//        try (Connection conn = DriverManager.getConnection("jdbc:mysql://89.108.72.116:3306/ivanchat", "ivan", "ruaFZ7rw4XSohkR1")) {
//            conn.setAutoCommit(true);
        try {
//            statement = conn.createStatement();
//            statement.execute("select * from orderitems where idorder=91945");
//            statement.execute("insert into 3d_orders (name_3d_orders) values (`ваыва111`)");
//            conn.commit();
//            statement.execute("CREATE TABLE `oleg` ( `id_klienta` INT(100) NOT NULL AUTO_INCREMENT , `name_klienta` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , PRIMARY KEY (`id_klienta`)) ENGINE = MyISAM CHARACTER SET utf8 COLLATE utf8_general_ci");
//             statement.execute("CREATE TABLE IF NOT EXISTS `oleg` ( `id_klienta` INT(100) NOT NULL AUTO_INCREMENT , `name_klienta` VARCHAR(50) COLLATE utf8_general_ci NOT NULL , PRIMARY KEY (`id_klienta`)) COLLATE utf8_general_ci");
/*            result = statement.execute("CREATE TABLE IF NOT EXISTS `oleg` ( `id_klienta` INT(100) NOT NULL AUTO_INCREMENT , `name_klienta` VARCHAR(50) COLLATE utf8_general_ci NOT NULL , PRIMARY KEY (`id_klienta`)) COLLATE utf8_general_ci");
            if (result) {
                System.out.println("Таблица успешно создана");
            } else {
                System.out.println("Таблица НЕ создана");
            }*/
            //Мы не можем получить информацию о том, успешно или нет отработал запрос CREATE TABLE IF NOT EXISTS, если SQL-запрос правильно и без ошибок написан, он отработает успешно в любом случае
            //statement.execute для запроса CREATE не выдаёт результатом true
            //Можно сделать CREATE TABLE без IF NOT EXISTS и тогда можно обработать исключение и понять успешно или нет отработал запрос
            //P.S.: обычно таблицы создаются заранее и их не создают в приложении(программе). Поэтому не буду усложнять конструкцию и просто выполню запрос и всё
            //Всё таки убрал IF NOT EXISTS, чтобы обработать ошибку создания таблицы users, если она уже существует
            statement.execute("CREATE TABLE `users` ( `id_klienta` INT(100) NOT NULL AUTO_INCREMENT, `login_klienta` VARCHAR(50) COLLATE utf8_general_ci NOT NULL, `password_klienta` VARCHAR(30) COLLATE utf8_general_ci NOT NULL, `nick_klienta` VARCHAR(50) COLLATE utf8_general_ci NOT NULL, `date_create_user` DATETIME, `last_enter` DATETIME, PRIMARY KEY (`id_klienta`)) COLLATE utf8_general_ci");
/*            statement.executeUpdate("insert into oleg (name_klienta) values ('sdsdfs')");
            statement.executeUpdate("insert into oleg (name_klienta) values ('sdsdfs22')");
            ResultSet resultSet = statement.executeQuery("select * from oleg");*/
            showMessage(Alert.AlertType.INFORMATION, "ИНФОРМАЦИЯ", "Таблица Пользователей успешно создана.");
        } catch (SQLException throwables) {
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Таблица Пользователей НЕ создана, т.к. уже существует.\nНет смысла создавать таблицу пользователей повторно.\n\nВы можете начать создавать новых Пользователей Чата.\nДля этого заполните поля ниже и нажмите кнопку Создать пользователя.");
        }
    }

    @FXML
        //Нажатие кнопки Удалить таблицу Пользователей
    void doDeleteTableUsers(ActionEvent event) {
        System.out.println("Нажатие кнопки Удалить таблицу Пользователей");
        try {/*(Connection conn = DriverManager.getConnection("jdbc:mysql://89.108.72.116:3306/ivanchat", "ivan", "ruaFZ7rw4XSohkR1")) {
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();*/
            statement.execute("DROP TABLE users");
            showMessage(Alert.AlertType.INFORMATION, "ИНФОРМАЦИЯ", "Таблица Пользователей успешно удалена.");
        } catch (SQLException throwables) {
            showMessage(Alert.AlertType.ERROR, "Ошибка", "Таблица Пользователей не удалена, т.к. не существует.\nСоздайте сначала таблицу пользователей, нажав кнопку Создать таблицу Пользователей.");
        }
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
//                String r =server.ClientHandler.idKlienta;// .BaseAuthService. .BaseAuthService .getIdByLoginAndPass(loginTextField.getText(), passwordPasswordField.getText());
//                System.out.println(BaseAuthService.entries);//тоже выдаёт null :-(
//                String r = serverService.getIdByLoginAndPass(loginTextField.getText(), passwordPasswordField.getText());
                authLabel.setText("Вы он-лайн, Ваш Ник: " + nick + ", Ваш id: " + SocketServerService.idKlienta);// + " Ваш id: " + server.ClientHandler.idKlienta
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

/*    @FXML
        //Этот метод будет вызываться каждый раз, когда мы будем переключать элемент списка
    void doLoginPasswordPodstavit(ActionEvent event) {
        loginTextField.setText(loginComboBox.getSelectionModel().getSelectedItem());
        passwordPasswordField.setText("password");
    }*/

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