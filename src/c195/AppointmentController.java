package c195;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AppointmentController {

    @FXML public Label apptHeader;
    @FXML public TextField apptTitleField;
    @FXML public TextArea apptDescField;
    @FXML public TextField apptContactField;
    @FXML public TextField apptLocationField;
    @FXML public DatePicker apptStartDateField;
    @FXML public ChoiceBox apptStartTimeSelect;
    @FXML public DatePicker apptFinishDateField;
    @FXML public ChoiceBox apptFinishTimeSelect;
    @FXML public ChoiceBox apptCustomerSelect;
    @FXML public Button apptOkBtn;
    @FXML public Button apptCancelBtn;

    @FXML
    public void initialize() throws Exception {
        //initializeUI();
    }

    public void handleApptOk(ActionEvent actionEvent) {
    }

    public void handleApptCancel(ActionEvent actionEvent) {
    }
}
