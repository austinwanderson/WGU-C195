package c195;

import c195.Models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AppointmentController {

    @FXML public AnchorPane ap;
    @FXML public Label apptHeader;
    @FXML public Label hiddenUserIdLabel;
    @FXML public Label hiddenUsernameLabel;
    @FXML public Label apptErrorMessage;
    @FXML public TextField apptTitleField;
    @FXML public TextArea apptDescField;
    @FXML public ChoiceBox apptContactSelect;
    @FXML public TextField apptLocationField;
    @FXML public TextField apptTypeField;
    @FXML public DatePicker apptDateField;
    @FXML public ChoiceBox apptStartTimeSelect;
    @FXML public DatePicker apptFinishDateField;
    @FXML public ChoiceBox apptFinishTimeSelect;
    @FXML public ChoiceBox apptCustomerSelect;
    @FXML public Button apptOkBtn;
    @FXML public Button apptCancelBtn;
    @FXML public Label hiddenApptLabel;
    @FXML public Label hiddenContactLabel;
    @FXML public Label hiddenStartLabel;
    @FXML public Label hiddenFinishLabel;
    @FXML public Label hiddenCustomerLabel;

    Parent root;
    Stage stage;

    private ObservableList<String[]> names;


    @FXML
    public void initialize() throws Exception {
        populateDateTimeFields();
        populateCustomerSelect();
        populateContactSelect();
    }

    private void populateContactSelect() throws SQLException {
        SqlDriver db = new SqlDriver();
        ObservableList<String[]> n = db.getContacts();
        ObservableList<Object> l = FXCollections.observableArrayList();
        n.forEach((name) -> {
            l.add(name[0] + ": " + name[1] + " (" + name[2] + ")");
        });
        apptContactSelect.setItems(l);

    }

    private void populateCustomerSelect() throws SQLException {
        SqlDriver db = new SqlDriver();
        ObservableList<String[]> n = db.getCustomerNames();
        setNames(n);
        ObservableList<Object> l = FXCollections.observableArrayList();
        n.forEach((name) -> {
            l.add(name[1]);
        });
        apptCustomerSelect.setItems(l);
    }

    private void populateDateTimeFields() {
        apptDateField.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY );
            }
        });

        apptFinishDateField.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY );
            }
        });

        ZonedDateTime estdate = ZonedDateTime.of(2020,1,1,8,0,0,0,ZoneId.of("America/New_York"));
        ZonedDateTime localdate = estdate.withZoneSameInstant(ZoneOffset.systemDefault());

        ObservableList<Object> startItems = FXCollections.observableArrayList();
        ObservableList<Object> finishItems = FXCollections.observableArrayList();
        for (int i=0;i<20;i++) {
            String str = estdate.format(DateTimeFormatter.ofPattern("hh:mm a")) + " EST (" + localdate.format(DateTimeFormatter.ofPattern("hh:mm a z")) + ")";

            if (i != 19) {
                startItems.add(str);
            }
            if (i != 0) {
                finishItems.add(str);
            }
            estdate = estdate.plusMinutes(30);
            localdate = localdate.plusMinutes(30);
        }
        apptStartTimeSelect.setItems(startItems);
        apptFinishTimeSelect.setItems(finishItems);
    }

    private void createNewAppt() throws IOException {
        String apptTitle = apptTitleField.getText();
        String apptDesc = apptDescField.getText();
        String apptContact = (apptContactSelect.getValue() != null) ? apptContactSelect.getValue().toString().split(":")[0] : "";
        String apptLocation = apptLocationField.getText();
        String apptType = apptTypeField.getText();
        String apptDate = (apptDateField.getValue() != null) ? apptDateField.getValue().toString() : "";
        String apptStartTime = (apptStartTimeSelect.getValue() != null) ? apptStartTimeSelect.getValue().toString() : "";
        String apptFinishTime = (apptFinishTimeSelect.getValue() != null) ? apptFinishTimeSelect.getValue().toString() : "";
        String apptCustomer = (apptCustomerSelect.getValue() != null) ? apptCustomerSelect.getValue().toString() : "";
        String userId = hiddenUserIdLabel.getText();

        final String[] customerId = new String[1];
        getNames().forEach((name) -> {
            if (apptCustomer == name[1]) {
                customerId[0] = name[0];
            }
        });

        Boolean validated = validateInput(apptTitle, apptDesc, apptContact, apptLocation, apptType, apptDate,
                apptStartTime, apptFinishTime,customerId[0], userId, false, "");
        if (validated) {
            Appointment newAppt = new Appointment(apptTitle, apptDesc, apptType, apptContact, apptLocation, apptDate, apptStartTime,
                    apptDate, apptFinishTime, customerId[0], userId);
            boolean created = newAppt.pushToDatabase();
            if (created) {
                loadHomeScreen();
            } else {
                //TODO: error message
            }
        }
    }

    private void loadHomeScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("main.fxml")));
        root = loader.load();
        MainController mainCtrl = loader.getController();
        mainCtrl.setUser(hiddenUserIdLabel.getText(), hiddenUsernameLabel.getText());
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scheduler");
        stage.show();
    }

    private void updateAppt(String apptId) throws IOException {
        String apptTitle = apptTitleField.getText();
        String apptDesc = apptDescField.getText();
        String apptContact = (apptContactSelect.getValue() != null) ? apptContactSelect.getValue().toString().split(":")[0] : "";
        String apptLocation = apptLocationField.getText();
        String apptType = apptTypeField.getText();
        String apptDate = (apptDateField.getValue() != null) ? apptDateField.getValue().toString() : "";
        String apptStartTime = (apptStartTimeSelect.getValue() != null) ? apptStartTimeSelect.getValue().toString() : "";
        String apptFinishTime = (apptFinishTimeSelect.getValue() != null) ? apptFinishTimeSelect.getValue().toString() : "";
        String apptCustomer = (apptCustomerSelect.getValue() != null) ? apptCustomerSelect.getValue().toString() : "";
        String userId = hiddenUserIdLabel.getText();
        System.out.println(userId + " 180");

        final String[] customerId = new String[1];
        getNames().forEach((name) -> {
            if (apptCustomer == name[1]) {
                customerId[0] = name[0];
            }
        });

        Boolean validated = validateInput(apptTitle, apptDesc, apptContact, apptLocation, apptType, apptDate,
                apptStartTime, apptFinishTime,customerId[0], userId, true, apptId);
        if (validated) {
            System.out.println("Updating appt: " + apptId);
            Appointment newAppt = new Appointment(apptTitle, apptDesc, apptType, apptContact, apptLocation, apptDate, apptStartTime,
                    apptDate, apptFinishTime, customerId[0], userId);
            boolean updated = newAppt.updateApptById(apptId);
            if (updated) {
                loadHomeScreen();
            } else {
                //TODO: error message
            }
        }
    }

    public void handleApptOk(ActionEvent actionEvent) throws IOException {

        if (hiddenApptLabel.getText() == "") {
            createNewAppt();
        } else {
            updateAppt(hiddenApptLabel.getText());
        }
    }

    private Boolean validateInput(String apptTitle, String apptDesc, String apptContact, String apptLocation, String apptType,
                                  String date,  String apptStartTime, String apptFinishTime,
                                  String apptCustomer, String userId, Boolean updatingAppt, String apptId) {

        if (apptTitle.length() != 0 && apptDesc.length() != 0 && apptContact.length() != 0 && apptLocation.length() != 0 &&
                apptType.length() != 0 && apptStartTime.length() != 0 && apptFinishTime.length() != 0 &&
                apptCustomer.length() != 0 && userId.length() != 0 && date.length() != 0) {
            String st = (date + " " + apptStartTime.substring(0,12));
            String ft = (date + " " + apptFinishTime.substring(0,12));
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a z");
            ZonedDateTime start = ZonedDateTime.parse(st, f).withZoneSameInstant(ZoneId.of("+0"));
            ZonedDateTime finish = ZonedDateTime.parse(ft, f).withZoneSameInstant(ZoneId.of("+0"));
            if (finish.isAfter(start)) {
                try {
                    if (checkForApptOverlap(apptCustomer, start, finish, apptId)) {
                        return true;
                    }
                    apptErrorMessage.setText("Customer has overlapping appt. time.");
                    apptErrorMessage.setOpacity(1);
                    return false;
                } catch (SQLException e) {
                    e.printStackTrace();
                    apptErrorMessage.setText("Unable to validate customer appt. times.");
                    apptErrorMessage.setOpacity(1);
                    return false;
                }
            }
            apptErrorMessage.setText("Finish time needs to be after start time.");
            apptErrorMessage.setOpacity(1);
            return false;
        }
        apptErrorMessage.setText("All fields need to be filled out.");
        apptErrorMessage.setOpacity(1);
        return false;
    }

    private boolean checkForApptOverlap(String apptCustomer, ZonedDateTime startTime, ZonedDateTime finishTime, String apptId) throws SQLException {
        SqlDriver db = new SqlDriver();
        return db.checkValidApptTime(apptCustomer, startTime, finishTime, apptId);
    }

    public void handleApptCancel(ActionEvent actionEvent) throws IOException {
        loadHomeScreen();
    }

    public ObservableList<String[]> getNames() {
        return names;
    }

    public void setNames(ObservableList<String[]> names) {
        this.names = names;
    }

    public void setCustomerValue(String customer) {
        ObservableList items = apptCustomerSelect.getItems();
        Boolean done = false;
        int i = 0;
        Iterator j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString();
            if (value.equals(customer)) {
                System.out.println("true");
                apptCustomerSelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }
    }

    public void setContactValue(String contact) {

        ObservableList items = apptContactSelect.getItems();
        Boolean done = false;
        int i = 0;
        Iterator j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString().split(":")[0];
            if (value.equals(contact)) {
                System.out.println("true");
                apptContactSelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }
    }

    public void setStartAndFinish(String s, String f) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
        ZonedDateTime st = ZonedDateTime.parse(s + " UTC", frmt).withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime fn = ZonedDateTime.parse(f + " UTC", frmt).withZoneSameInstant(ZoneId.of("America/New_York"));
        String start = st.format(DateTimeFormatter.ofPattern("hh:mm a")) + " EST";
        String finish = fn.format(DateTimeFormatter.ofPattern("hh:mm a")) + " EST";
        String date = st.format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
        System.out.println(start);
        System.out.println(finish);

        ObservableList items = apptStartTimeSelect.getItems();
        Boolean done = false;
        int i = 0;
        Iterator j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString();
            if (value.contains(start)) {
                apptStartTimeSelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }

        items = apptFinishTimeSelect.getItems();
        done = false;
        i = 0;
        j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString();
            if (value.contains(finish)) {
                apptFinishTimeSelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }

        apptDateField.setValue(LocalDate.parse(date, DateTimeFormatter.ofPattern("d/MM/yyyy")));
    }

    public void setApptId(String id) {
        hiddenApptLabel.setText(id);
    }

    public void setUser(String id, String user) {
        hiddenUsernameLabel.setText(user);
        hiddenUserIdLabel.setText(id);
    }
}













