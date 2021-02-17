package server;

import client.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    public static List<Entry> entries;
    public static Connection conn = null;//будем использовать одно соединение на всю программу
    public static Statement statement;//будем использовать одно соединение на всю программу

    public BaseAuthService() {
        entries = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Сервер: Драйвер базы данных MySQL успешно загружен!");
        } catch (Exception ex) {
            System.out.println("Сервер: Драйвер MySQL НЕ загрузился.");
            System.out.println(ex);
        }
        //подключаемся к базе данных и будем использовать одно соединение на всю программу
        try {
            conn = DriverManager.getConnection("jdbc:mysql://89.108.72.116:3306/ivanchat", "ivan", "ruaFZ7rw4XSohkR1");
            conn.setAutoCommit(true);
            statement = conn.createStatement();
            System.out.println("Сервер: Соединение с базой данных успешно произведено!");
        } catch (Exception ex) {
            System.out.println("Сервер: Соединение с базой данных не произведено.");
            System.out.println(ex);
        }
        ResultSet resultSet;
        //загружаем всех существующих на данный момент пользователей в Entry и только они на данный момент могут авторизовываться. Если в таблицу users будет добавлен новый пользователь, то он не сможет авторизваться, пока Сервер чата не будет перезапущен
        try {
            resultSet = statement.executeQuery("select id_klienta, login_klienta, password_klienta, nick_klienta from users order by id_klienta");
            while (resultSet.next()) {
                entries.add(new Entry(resultSet.getString("id_klienta"), resultSet.getString("login_klienta"), resultSet.getString("password_klienta"), resultSet.getString("nick_klienta")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

/*        entries.add(new Entry("ivan", "password", "nick_ivan"));
        entries.add(new Entry("sharik", "password", "nick_sharik"));
        entries.add(new Entry("petr", "password", "nick_petr"));*/
//        System.out.println(entries);
    }

    private class Entry {
        private String id;
        private String login;
        private String password;
        private String nick;

        public Entry(String id, String login, String password, String nick) {
            this.id = id;
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис авторизации запушен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис авторизации остановлен");
    }

    @Override
    public String getNickByLoginAndPass(String login, String password) {
        for (Entry entry : entries) {
            if (login.equals(entry.login) && password.equals(entry.password)) {
                return entry.nick;
            }
        }
        return null;
    }

    @Override
    public String getIdByLoginAndPass(String login, String password) {
        for (Entry entry : entries) {
            if (login.equals(entry.login) && password.equals(entry.password)) {
                return entry.id;
            }
        }
        return null;
    }
}
