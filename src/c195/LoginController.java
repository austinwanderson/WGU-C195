package c195;

import c195.Models.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * LoginController handles all events for the login UI.
 *
 *
 * @author  Austin Anderson
 * @version 1.0
 * @since   2021-06-10
 */
public class LoginController {

    @FXML private GridPane ap;
    @FXML private Label loginErrorMessage;
    @FXML private Label loginMainLabel;
    @FXML private Label loginPasswordLabel;
    @FXML private Label loginUsernameLabel;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginUsernameField;
    @FXML private Button loginButton;
    @FXML private Label loginLanguageLabel;
    @FXML private ChoiceBox languageChoiceSelect;
    @FXML private Label loginZoneLabel;

    public static String currentLanguage;

    Parent root;
    Stage stage;

    /**
     * Called when the login is initialized displaying the zone id
     * and filling the language choice select.
     */
    @FXML
    public void initialize() {
        loginZoneLabel.setText(java.time.ZoneId.systemDefault().toString());
        languageChoiceSelect.setItems(FXCollections.observableArrayList("English", "French"));
        setLoginLanguage("");
    }

    /**
     * Changes the login language of the UI components.
     *
     * @param language language selected
     */
    public void setLoginLanguage(String language) {
        try {
            ResourceBundle rb;
            if (language != "") {
                currentLanguage = language;
                rb = ResourceBundle.getBundle("c195.Properties.lang", Locale.forLanguageTag(language));
            } else {
                currentLanguage = Locale.getDefault().toString().substring(0,2);
                rb = ResourceBundle.getBundle("c195.Properties.lang", Locale.getDefault());
                if (Locale.getDefault().toString().substring(0,2) == "fr") { languageChoiceSelect.setValue("French"); }
                else { languageChoiceSelect.setValue("English"); }
            }
            loginUsernameLabel.setText(rb.getString("username"));
            loginPasswordLabel.setText(rb.getString("password"));
            loginLanguageLabel.setText(rb.getString("language"));
            loginButton.setText(rb.getString("button"));
            loginMainLabel.setText(rb.getString("header"));
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the login button clicked event.
     *
     * @param actionEvent click event
     * @exception SQLException db error
     * @exception IOException
     */
    public void handleLoginClicked(ActionEvent actionEvent) throws IOException, SQLException {
        boolean loggedin = verifyLogin(loginUsernameField.getText(), loginPasswordField.getText());
        if (loggedin) {
            User user = new User(loginUsernameField.getText());
            FXMLLoader loader = new FXMLLoader((getClass().getResource("main.fxml")));
            root = loader.load();
            MainController mainCtrl = loader.getController();
            mainCtrl.setUser(user.getUserId(), user.getUsername());
            mainCtrl.getApptsWithinFifteenMinutes();
            stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Scheduler");
            stage.show();
        }
    }

    /**
     * Handles when a user selects a new language.
     *
     * @param actionEvent click event
     */
    public void handleLanguageSelect(ActionEvent actionEvent) {
        String lang = languageChoiceSelect.getValue().toString();
        if (lang == "French") {
            setLoginLanguage("fr");
        } else {
            setLoginLanguage("en");
        }
    }

    /**
     * Handles verifying login credentials
     *
     * @param username given username
     * @param password given password
     * @return boolean true if credentials are accepted.
     * @exception SQLException db error
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        SqlDriver db = new SqlDriver();
        ResultSet credentials = db.getUserCredentials(username, password);
        if (credentials == null) {
            String errorMessage = ResourceBundle.getBundle("c195.Properties.lang",
                    Locale.forLanguageTag(currentLanguage)).getString("db_connection_error");
            loginErrorMessage.setText(errorMessage);
            loginErrorMessage.setOpacity(1);
            writeLoginAttemptToFile(username, errorMessage);
            return false;
        } else if (!credentials.first()) {
            String errorMessage = ResourceBundle.getBundle("c195.Properties.lang",
                    Locale.forLanguageTag(currentLanguage)).getString("incorrect_credentials");
            loginErrorMessage.setText(errorMessage);
            loginErrorMessage.setOpacity(1);
            writeLoginAttemptToFile(username, errorMessage);
            return false;
        } else {
            writeLoginAttemptToFile(username, "Successful Login.");
            return true;
        }
    }

    /**
     * Handles verifying login credentials
     *
     * @param user given user id
     * @param reason login result
     * @exception IOException IO error
     */
    public void writeLoginAttemptToFile(String user, String reason) {
        try {
            String data = "User ID: " + user + " | " + "Timestamp: " +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
                    + " | " + "Result: " + reason + "\n";
            FileOutputStream file = new FileOutputStream("login_requirements.txt", true);
            file.write(data.getBytes());
            file.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
