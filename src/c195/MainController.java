package c195;

import c195.Models.Appointment;
import c195.Models.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class MainController {

    @FXML public AnchorPane ap;
    @FXML public GridPane weekGrid;
    @FXML public TabPane mainTabs;
    @FXML public Label weekSundayDayLabel;
    @FXML public Label weekMondayDayLabel;
    @FXML public Label weekTuesdayDayLabel;
    @FXML public Label weekWednesdayDayLabel;
    @FXML public Label weekThursdayDayLabel;
    @FXML public Label weekFridayDayLabel;
    @FXML public Label weekSaturdayDayLabel;
    @FXML public Accordion weekAccordionApptList;
    @FXML public Label weekToWeekLabel;
    @FXML public Button nextWeekBtn;
    @FXML public Button previousWeekBtn;
    @FXML public Accordion monthAccordionApptList;
    @FXML public Button addNewCustomerBtn;
    @FXML public Button updateCustomerBtn;
    @FXML public Button deleteCustomerBtn;
    @FXML public Button createApptBtn;
    @FXML public Button updateApptBtn;
    @FXML public Button deleteApptBtn;
    @FXML public TableView apptTable;
    @FXML public TableView customerTable;
    @FXML public Label monthYearLabel;
    @FXML public Button nextMonthBtn;
    @FXML public Button previousMonthBtn;
    @FXML public GridPane calendarGrid;
    @FXML public Label hiddenMainApptLabel;
    @FXML public Label hiddenMainCustomerLabel;
    @FXML public Label hiddenUsernameLabel;
    @FXML public Label hiddenUserIdLabel;

    public static Node[][] calendarNodes;
    public static Node[] weekDayNodes;
    public static Node[] weekApptNodes;
    public static int calendarWidth = 7;
    public static int calendarHeight = 6;
    public static int currentMonth = 0;
    public static int currentDay = 0;
    public static int currentYear = 0;
    public static LocalDate firstDayOfWeek;
    public static LocalDate lastDayOfWeek;
    public static LocalDate today;
    public static LocalDate firstOfMonth;
    public static Boolean weekInitialized = false;
    public static Boolean monthInitialized = false;
    public static Boolean customersInitialized = false;
    public static Boolean appointmentsInitialized = false;
    public Map<Integer, List<String[]>> weekData;

    Parent root;
    Stage stage;
    Appointment selectedAppt;
    Customer selectedCustomer;

    @FXML
    public void initialize() throws Exception {
        weekInitialized = false;
        monthInitialized = false;
        customersInitialized = false;
        appointmentsInitialized = false;

        initializeUI();
        initWeek();
    }

    public void initializeUI() throws SQLException {
        today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentDay = today.getDayOfMonth();
        currentYear = today.getYear();
        firstOfMonth = LocalDate.of(today.getYear(),today.getMonthValue(),1);

        initTabs();

        System.out.println(mainTabs.getSelectionModel().getSelectedItem().getText());
    }

    private void initWeek() {
        weekInitialized = true;
        buildWeek();
        updateWeekDates(today);
        try {
            fillWeekCalendar(firstDayOfWeek, lastDayOfWeek);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void initTabs() {
        ObservableList<Tab> tabs = mainTabs.getTabs();
        tabs.forEach((tab) -> {
            tab.setOnSelectionChanged((e) -> {
                if (tab.isSelected()) {
                    if (tab.getText().equals("Week View") && !weekInitialized) {
                        initWeek();
                    } else if (tab.getText().equals("Month View") && !monthInitialized) {
                        monthInitialized = true;
                        buildCalendar();
                        updateCalendarDates(firstOfMonth);
                    } else if (tab.getText().equals("Manage Customers") && !customersInitialized) {
                        customersInitialized = true;
                        try {
                            updateCustomersTable();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else if (tab.getText().equals("Manage Appointments") && !appointmentsInitialized) {
                        appointmentsInitialized = true;
                        try {
                            updateApptsTable();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    private void fillWeekCalendar(LocalDate f, LocalDate l) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<Integer, List<String[]>> data = db.getApptsByWeek(f,l);
        weekData = data;
        int i = 0;
        for (i=0;i<data.size();i++) {
            Pane dayPane = (Pane) weekApptNodes[i];
            VBox dayList = (VBox) dayPane.getChildren().get(0);
            List<String[]> appts = data.get(i);
            for (String[] appt : appts) {
                Label apptLabel = new Label(appt[2]);
                apptLabel.setPadding(new Insets(5));
                Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 13);
                apptLabel.setFont(font);
                dayList.getChildren().add(apptLabel);
            }
        }
    }

    private void clearWeekAppts() {
        for (int i=0;i<weekApptNodes.length;i++) {
            Pane dayPane = (Pane) weekApptNodes[i];
            VBox dayList = (VBox) dayPane.getChildren().get(0);
            dayList.getChildren().clear();
        }
    }

    private void updateCustomersTable() throws SQLException {
        SqlDriver db = new SqlDriver();
        List<String[]> customers = db.getCustomersForTable();

        customerTable.getColumns().clear();
        customerTable.getItems().clear();

        TableColumn customerIdColumn = new TableColumn("Id");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        TableColumn customerNameColumn = new TableColumn("Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        TableColumn customerAddressColumn = new TableColumn("Address");
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("Address"));
        TableColumn customerPhoneColumn = new TableColumn("Phone");
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("Phone"));

        customerTable.getColumns().addAll(customerIdColumn, customerNameColumn, customerAddressColumn, customerPhoneColumn);
        for (String[] cust : customers) {
            Customer tc = new Customer(cust[0], cust[1], cust[2], cust[3], cust[4], cust[5], cust[6], true);
            customerTable.getItems().add(tc);
        }

        customerTable.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 1) {
                customerSelect();
            }
        });

    }

    private void customerSelect() {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();
            updateCustomerBtn.setDisable(false);
            deleteCustomerBtn.setDisable(false);
        }
    }

    private void updateApptsTable() throws SQLException {
        SqlDriver db = new SqlDriver();
        List<String[]> appts = db.getApptsForTable();

        apptTable.getColumns().clear();
        apptTable.getItems().clear();

        TableColumn apptIdColumn = new TableColumn("Id");
        apptIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        TableColumn apptTitleColumn = new TableColumn("Title");
        apptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("Title"));
        TableColumn apptStartColumn = new TableColumn("Start");
        apptStartColumn.setCellValueFactory(new PropertyValueFactory<>("Start"));
        TableColumn apptEndColumn = new TableColumn("End");
        apptEndColumn.setCellValueFactory(new PropertyValueFactory<>("End"));
        TableColumn apptContactColumn = new TableColumn("Contact");
        apptContactColumn.setCellValueFactory(new PropertyValueFactory<>("Contact"));

        apptTable.getColumns().addAll(apptIdColumn, apptTitleColumn, apptStartColumn, apptEndColumn, apptContactColumn);
        appts.forEach((appt) -> {
            try {
                Appointment ap = new Appointment(appt[0], appt[1], appt[2], appt[3], db.getContactNameById(appt[4]));
                apptTable.getItems().add(ap);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Appointment ap = new Appointment(appt[0], appt[1], appt[2], appt[3], appt[4]);
                apptTable.getItems().add(ap);
            }
        });

        apptTable.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 1) {
                apptSelect();
            }
        });
    }

    public void apptSelect() {
        if (apptTable.getSelectionModel().getSelectedItem() != null) {
            selectedAppt = (Appointment) apptTable.getSelectionModel().getSelectedItem();
            updateApptBtn.setDisable(false);
            deleteApptBtn.setDisable(false);
        }
    }

    private void buildCalendar() {
        calendarNodes = new Node[calendarWidth][calendarHeight];
        for (Node child : calendarGrid.getChildren()) {
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            calendarNodes[column][row] = child;
        }
    }

    private void buildWeek() {
        weekDayNodes = new Node[calendarWidth];
        weekApptNodes = new Node[calendarWidth];
        for (Node child : weekGrid.getChildren()) {
            System.out.println(child);
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            if (row == 0) { weekDayNodes[column] = child; }
            if (row == 1) {
                child.setOnMouseClicked((event) -> {
                    GridPane grid = (GridPane) child.getParent();
                    int i = 0;
                    int index = 0;
                    LocalDate daySelected = firstDayOfWeek;
                    while (i < 7) {
                        Pane selectedPane = (Pane) grid.getChildren().get(i+7);
                        Pane dayPane = (Pane) grid.getChildren().get(i);
                        if (selectedPane == child) {
                            index = i;
                            selectedPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            dayPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            daySelected = firstDayOfWeek.plusDays(i);
                        } else {
                            selectedPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            dayPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        }
                        i += 1;
                    }
                    fillDayAppointments(daySelected, weekAccordionApptList, index);
                });
                weekApptNodes[column] = child;
            }
        }
    }

    private void fillDayAppointments(LocalDate daySelected, Accordion list, int index) {
        System.out.println(daySelected);
        System.out.println(list);
        System.out.println(index);
        list.getPanes().clear();
        List<String[]> appts = weekData.get(index);
        for (String[] appt : appts) {
            GridPane grid = new GridPane();
            grid.setVgap(4);
            grid.setPadding(new Insets(5, 5, 5, 5));
            grid.add(new Label("Appointment ID:  " + appt[1]),0,0);
            grid.add(new Label("Description:  " + appt[3]),0,1);
            grid.add(new Label("Location:  " + appt[4]),0,2);
            grid.add(new Label("Type:  " + appt[5]),0,3);
            grid.add(new Label("Start:  " + appt[0]),0,4);
            grid.add(new Label("End:  " + appt[6]),0,5);
            grid.add(new Label("Customer:  " + appt[7]),0,6);
            grid.add(new Label("Customer ID:  " + appt[10]),0,7);
            grid.add(new Label("Contact:  " + appt[8]),0,8);
            grid.add(new Label("Contact ID:  " + appt[11]),0,9);
            //Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 14);
            TitledPane titledPane = new TitledPane(appt[2], grid);
            list.getPanes().add(titledPane);
        }

        //appointments.add(i,new String[]{results.getString("start"),results.getString("appointment_id"), results.getString("title"),
        //                        results.getString("description"),results.getString("location"),results.getString("type"),results.getString("end"),
        //                        results.getString("customer_name"),results.getString("contact_name"),results.getString("email"),results.getString("customer_id"),
        //                        results.getString("contact_id")});
        //TODO
    }

    private void updateWeekDates(LocalDate day) {
        int dayOfWeek = day.getDayOfWeek().getValue();
        LocalDate monday = day.minusDays(dayOfWeek-1);
        int i = 1;
        while (i <= 7) {
            LocalDate currentDay;
            Pane dayBlock;
            if (i == 7) {
                currentDay = monday.minusDays(1);
                dayBlock = (Pane) weekDayNodes[0];
            } else {
                currentDay = monday.plusDays(i-1);
                dayBlock = (Pane) weekDayNodes[i];
            }
            Label dayLabel = (Label) dayBlock.getChildren().get(0);
            dayLabel.setText(currentDay.getMonthValue() + "/" + currentDay.getDayOfMonth());
            i += 1;
        }

        firstDayOfWeek = monday.minusDays(1);
        lastDayOfWeek = monday.plusDays(5);

        String weekLabel =
                firstDayOfWeek.getMonthValue() + "/" + firstDayOfWeek.getDayOfMonth() + "/" + firstDayOfWeek.getYear() + " - " +
                lastDayOfWeek.getMonthValue() + "/" + lastDayOfWeek.getDayOfMonth() + "/" + lastDayOfWeek.getYear();
        if (weekToWeekLabel == null) {
            weekToWeekLabel = (Label) Main.main_scene.lookup("#weekToWeekLabel");
        }
        weekToWeekLabel.setText(weekLabel);
    }

    private int getDaysInMonth(int year, int month) {
        return month != 2 ?
                31 - (((month - 1) % 7) % 2) :
                28 + (year % 4 == 0 ? 1 : 0) - (year % 100 == 0 ? 1 : 0) + (year % 400 == 0 ? 1 : 0);
    }

    public void updateCalendarDates(LocalDate firstOfMonth) {
        int dayCount = 1;
        int firstOfMonthDay = firstOfMonth.getDayOfWeek().getValue();
        if (firstOfMonthDay == 7) {
            firstOfMonthDay = 0;
        }
        int daysInMonth = getDaysInMonth(firstOfMonth.getYear(), firstOfMonth.getMonthValue());
        for (int i = 0 ; i < calendarHeight ; i++) {
            for (int j = 0; j < calendarWidth; j++) {
                Pane dayBlock = (Pane) calendarNodes[j][i];
                VBox dayVbox = (VBox) dayBlock.getChildren().get(0);
                Label day = (Label) dayVbox.getChildren().get(0);
                if (firstOfMonthDay > 0) {
                    firstOfMonthDay -= 1;
                    day.setText("");
                } else if (dayCount <= daysInMonth) {
                    day.setText(String.valueOf(dayCount));
                    dayCount++;
                } else {
                    day.setText("");
                }
            }
        }

        String monthName = firstOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
        if (monthYearLabel == null) {
            monthYearLabel = (Label) Main.main_scene.lookup("#monthYearLabel");
        }
        monthYearLabel.setText(monthName + " " + currentYear);
    }

    public void handleAddNewCustomer(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("customer.fxml")));
        root = loader.load();
        CustomerController custCtrl = loader.getController();
        custCtrl.setUser(hiddenUserIdLabel.getText(),hiddenUsernameLabel.getText());
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add Customer");
        stage.show();
    }

    public void handleUpdateCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("customer.fxml")));
        root = loader.load();
        CustomerController custCtrl = loader.getController();
        SqlDriver db = new SqlDriver();
        Map<String, String> editingCustomer = db.getCustomerById(selectedCustomer.getId());
        custCtrl.customerHeader.setText("Update Customer ID " + editingCustomer.get("customer_id"));
        custCtrl.custAddressField.setText(editingCustomer.get("address"));
        custCtrl.custNameField.setText(editingCustomer.get("customer_name"));
        custCtrl.custPostalCodeField.setText(editingCustomer.get("postal_code"));
        custCtrl.custPhoneField.setText(editingCustomer.get("phone"));
        custCtrl.hiddenCustomerIdLabel.setText(editingCustomer.get("customer_id"));
        custCtrl.hiddenDivisionIdLabel.setText(editingCustomer.get("division_id"));
        custCtrl.setUser(hiddenUserIdLabel.getText(),hiddenUsernameLabel.getText());
        String[] cp = custCtrl.getCountryAndProvince(editingCustomer.get("division_id"));
        custCtrl.setCountryValue(cp[0]);
        custCtrl.setStateValue(cp[1]);
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Update Customer");
        stage.show();
    }

    public void handleDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<String, String> editingAppt = db.getCustomerById(selectedCustomer.getId());
        boolean deleted = db.deleteCustomerById(selectedCustomer.getId());
        if (deleted) {
            updateCustomersTable();
            updateApptsTable();
        }
    }

    public void handleCreateNewAppt(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("appointment.fxml")));
        root = loader.load();
        AppointmentController apptCtrl = loader.getController();
        apptCtrl.setUser(hiddenUserIdLabel.getText(),hiddenUsernameLabel.getText());
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Create Appointment");
        stage.show();
    }

    public void handleUpdateAppt(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("appointment.fxml")));
        SqlDriver db = new SqlDriver();
        root = loader.load();
        AppointmentController apptCtrl = loader.getController();
        Map<String, String> editingAppt = db.getApptById(selectedAppt.getId());

        apptCtrl.apptTitleField.setText(editingAppt.get("title"));
        apptCtrl.apptDescField.setText(editingAppt.get("description"));
        apptCtrl.apptLocationField.setText(editingAppt.get("location"));
        apptCtrl.apptTypeField.setText(editingAppt.get("type"));
        apptCtrl.apptHeader.setText("Update Appointment ID " + editingAppt.get("appointment_id"));
        String customer_name = db.getCustomerNameById(editingAppt.get("customer_id"));
        apptCtrl.setContactValue(editingAppt.get("contact_id"));
        apptCtrl.setCustomerValue(customer_name);
        apptCtrl.setStartAndFinish(editingAppt.get("start"), editingAppt.get("end"));
        apptCtrl.setUser(hiddenUserIdLabel.getText(),hiddenUsernameLabel.getText());
        apptCtrl.setApptId(editingAppt.get("appointment_id"));

        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Update Appointment");
        stage.show();
    }

    public void handleDeleteAppt(ActionEvent actionEvent) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<String, String> editingAppt = db.getApptById(selectedAppt.getId());
        boolean deleted = db.deleteApptById(selectedAppt.getId());
        if (deleted) {
            updateApptsTable();
        }
    }

    public void handleGoToNextWeek(ActionEvent actionEvent) throws SQLException {
        firstDayOfWeek = lastDayOfWeek.plusDays(2);
        clearWeekAppts();
        updateWeekDates(firstDayOfWeek);
        fillWeekCalendar(firstDayOfWeek, lastDayOfWeek);
    }

    public void handleGoToPreviousWeek(ActionEvent actionEvent) throws SQLException {
        lastDayOfWeek = firstDayOfWeek.minusDays(2);
        clearWeekAppts();
        updateWeekDates(lastDayOfWeek);
        fillWeekCalendar(firstDayOfWeek, lastDayOfWeek);
    }

    public void handleGoToNextMonth(ActionEvent actionEvent) {
        currentMonth += 1;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear += 1;
        }
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        updateCalendarDates(date);
    }

    public void handleGoToPreviousMonth(ActionEvent actionEvent) {
        currentMonth -= 1;
        if (currentMonth == 0) {
            currentMonth = 12;
            currentYear -= 1;
        }
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        updateCalendarDates(date);
    }

    public void setUser(String id, String user) {
        hiddenUserIdLabel.setText(id);
        hiddenUsernameLabel.setText(user);
    }
}






