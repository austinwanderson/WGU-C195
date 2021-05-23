package c195;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;



public class Main extends Application {

    public static Parent login;
    public static Parent main_app;
    public static Stage main_stage;

    public static Scene main_scene;
    public static Scene login_scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        //
        try {
            primaryStage.setTitle(ResourceBundle.getBundle("c195.Properties.lang", Locale.getDefault()).getString("title"));
        } catch (MissingResourceException e) {
            primaryStage.setTitle("Login");
        }
        login_scene = new Scene(login, 443, 272);
        //main_scene = new Scene(main_app, 953, 620);
        //System.out.println(main_scene);
        primaryStage.setScene(login_scene);
        main_stage = primaryStage;
        main_stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
