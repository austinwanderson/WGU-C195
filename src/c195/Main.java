package c195;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    public static Parent login;
    public static Parent main_app;

    @Override
    public void start(Stage primaryStage) throws Exception{
        login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        main_app = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(login, 443, 272));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
