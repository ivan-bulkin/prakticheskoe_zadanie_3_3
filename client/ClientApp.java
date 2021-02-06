package client;

//Не удалось запустить несколько экзепляров одной программы. То, как другие это делают и выкладываю, как делать в чате Инстраграмм наверное работает только для Maven
//Но я не стал заморачиваться Maven'ом, а сделал три ClientApp, их можно по очереди запустить и тестировать работу
//Удалось запустить несколько экземпляров программы: Ни какой Maven не нужен, чтобы несколько раз запускать программу, надо всего-лишь в Мендю запуска программы Run/Debug Configuration добавить опцию Allow multiple instances
//Для удобства в каждом можно подставить логин/пароль пользователя и подключится к серверу
//Сделал loginComboBox, чтобы из него можно было подставлять пользователей в окошко логин/пароль для удобства тестирования
//Доделано:
//Прописано событие, которое происходит при нажатии крестика закрытия окна JavaFX, корректно пользователь выходи из чата
//Разобрался и исправил собственную ошибку в получении Ника пользователя при авторизации в чате
//Исправил ошибку, если клиент набирает /w ник_пользователя и далее не набирает ни какого сообщения

//Много всего сделано, но и много не сделано.
//Корректный выход не реализован, когда пытаещься зайти под тем же логином, под которым уже зашли, когда вводишь не верный пароль и т.д. и т.п., но с кодом я разобрался и сделал достаточно, чтобы вкурить ещё немного Java
//Один раз почему-то как-то криво работал курсор в окошке ввода сообщений - курсор стоял на месте, а текст появлялся справа от курсора, но было это всего один раз

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import server.MyServer;

public class ClientApp extends Application {

//    private ServerService serverService;//это переносим в кнопку Войти в чат

    @Override
    public void start(Stage primaryStage) throws Exception {

/*        serverService = new SocketServerService();//это переносим в кнопку Войти в чат
        serverService.openConnection();//Авторизуемся*/

        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("ИванЧат 2021");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);//Не разрешаем масштабировать
        primaryStage.show();

        //прописываем событие, которое срабатывает при закрытии онка - нажатие на крестик закрытия окна
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Thread.interrupted();//Останавливаем поток
                System.exit(0);//Выходим из программы
//                System.out.println("Stage is closing");
//                event.consume();
            }
        });
/*        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
            }
        });*/
    }

    public static void Main(String[] args) {
        launch(args);
    }



/*    //Класс, который всё запускает
    public class ServerApp {
        public static void main(String[] args) {
            new MyServer();//создание экземпляра класса MyServer
        }*/
/*    @Override

    public void stop(){
        System.out.println("Приложение закрывается!!!");
        //Здесь Вы можете прописать все действия при закрытии Вашего приложения.
    }*/
}
