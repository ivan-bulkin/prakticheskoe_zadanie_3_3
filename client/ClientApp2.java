package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

public class ClientApp2 extends Application {

//    private ServerService serverService;//это переносим в кнопку Войти в чат

    @Override
    public void start(Stage primaryStage) throws Exception {

/*        serverService = new SocketServerService();//это переносим в кнопку Войти в чат
        serverService.openConnection();//Авторизуемся*/

        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("ИванЧат 2021 второй экземпляр");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);//Не разрешаем масштабировать
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}