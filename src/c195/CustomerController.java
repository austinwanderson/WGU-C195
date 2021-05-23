package c195;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerController {

    @FXML public Label customerHeader;
    @FXML public TextField custNameField;
    @FXML public TextField custPostalCodeField;
    @FXML public ChoiceBox custCountryField;
    @FXML public Button custOkBtn;
    @FXML public Button custCancelBtn;
    @FXML public TextField custAddressField;
    @FXML public TextField custCityField;
    @FXML public ChoiceBox custStateField;
    @FXML public TextField custPhoneField;

    @FXML
    public void initialize() throws Exception {
        //initializeUI();
    }

    public void handleCustomerOk(ActionEvent actionEvent) {
    }

    public void handleCustomerCancel(ActionEvent actionEvent) {
    }
}
