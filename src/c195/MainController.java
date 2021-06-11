package c195;

import c195.Models.Appointment;
import c195.Models.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * MainController handles all events for the main scheduler UI.
 *
 *
 * @author  Austin Anderson
 * @version 1.0
 * @since   2021-06-10
 */
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
    @FXML public Button customerApptReportBtn;
    @FXML public Button contactScheduleReportBtn;
    @FXML public Button customerScheduleReportBtn;
    @FXML public TableView apptTable;
    @FXML public TableView customerTable;
    @FXML public TextArea reportsTextField;
    @FXML public Label monthYearLabel;
    @FXML public Button nextMonthBtn;
    @FXML public Button previousMonthBtn;
    @FXML public GridPane calendarGrid;
    @FXML public Label hiddenMainApptLabel;
    @FXML public Label hiddenMainCustomerLabel;
    @FXML public Label hiddenUsernameLabel;
    @FXML public Label hiddenUserIdLabel;
    @FXML public Label noApptsLabelWeek;
    @FXML public Label noApptsLabelMonth;
    @FXML public Label noApptsIn15Label;

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
    public static LocalDate lastOfMonth;
    public static Boolean weekInitialized = false;
    public static Boolean monthInitialized = false;
    public static Boolean customersInitialized = false;
    public static Boolean appointmentsInitialized = false;
    public Map<Integer, List<String[]>> weekData;
    public Map<Integer, List<String[]>> monthData;
    public Map<Integer, VBox> monthNodes;

    Parent root;
    Stage stage;
    Appointment selectedAppt;
    Customer selectedCustomer;

    /**
     * Called when the MainController is initialized.  Sets up
     * the UI.
     */
    @FXML
    public void initialize() throws Exception {
        weekInitialized = false;
        monthInitialized = false;
        customersInitialized = false;
        appointmentsInitialized = false;

        initializeUI();
        initWeek();
    }

    /**
     * Determines whether any appointments occur within 15 minutes of
     * login and shows an Alert if so.
     *
     * @exception SQLException db error
     */
    public void getApptsWithinFifteenMinutes() throws SQLException {
        SqlDriver db = new SqlDriver();
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
        DateTimeFormatter frmt_z = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
        DateTimeFormatter frmt_a = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String inFifteen = ZonedDateTime.now().plusMinutes(15).withZoneSameInstant(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(inFifteen);
        System.out.println(now);
        List<String[]> appts = db.getApptsWithin15Minutes(now, inFifteen);
        if (appts.size() > 0) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Appointment ID " + appts.get(0)[0] + " starts within 15 minutes at " +
                    ZonedDateTime.parse(appts.get(0)[1] + " UTC", frmt_z).withZoneSameInstant(ZoneId.systemDefault()).format(frmt_a));
            ((Stage) a.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
            a.show();
            noApptsIn15Label.setOpacity(0);
        } else {
            noApptsIn15Label.setOpacity(1);
        }
    }

    /**
     * Handles UI initialization
     *
     * @exception SQLException db error
     */
    public void initializeUI() throws SQLException {
        today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentDay = today.getDayOfMonth();
        currentYear = today.getYear();
        firstOfMonth = LocalDate.of(today.getYear(),today.getMonthValue(),1);
        lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1);

        initTabs();
    }

    /**
     * Initializes the Week UI of the main view.
     */
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

    /**
     * Initializes the Month UI of the main view.
     */
    private void initMonth() {
        monthInitialized = true;
        buildCalendar();
        updateCalendarDates(firstOfMonth);
        try {
            fillMonthCalendar(firstOfMonth, lastOfMonth);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Initializes the tabs on the main view and calls functions
     * to initialize each tab's view.  Lambda expression used to
     * loop through tabs elements more efficiently passing the tab
     * object.
     */
    private void initTabs() {
        ObservableList<Tab> tabs = mainTabs.getTabs();
        tabs.forEach((tab) -> {
            tab.setOnSelectionChanged((e) -> {
                if (tab.isSelected()) {
                    if (tab.getText().equals("Week View") && !weekInitialized) {
                        initWeek();
                    } else if (tab.getText().equals("Month View") && !monthInitialized) {
                        initMonth();
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

    /**
     * Handles updating the calendar with the month's appointments.
     *
     * @param f first day of month
     * @param l last day of month
     * @exception SQLException db error
     */
    private void fillMonthCalendar(LocalDate f, LocalDate l) throws SQLException {
        SqlDriver db = new SqlDriver();
        int daysInMonth = getDaysInMonth(f.getYear(), f.getMonthValue());
        Map<Integer, List<String[]>> data = db.getApptsByMonth(f,l, daysInMonth);
        monthData = data;
        int i = 0;
        for (i=1;i<=data.size();i++) {
            VBox dayList = (VBox) monthNodes.get(i);
            List<String[]> appts = data.get(i-1);
            String numOfAppts = ((appts.size() == 0) ? "" : (appts.size() == 1) ? String.valueOf(appts.size()) +
                    " appointment" : String.valueOf(appts.size()) + " appointments");

            Label apptLabel = new Label(numOfAppts);
            apptLabel.setPadding(new Insets(3));
            Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 13);
            apptLabel.setFont(font);
            dayList.getChildren().add(apptLabel);
        }
    }

    /**
     * Handles updating the week UI with the week's appointments.
     *
     * @param f first day of week
     * @param l last day of week
     * @exception SQLException db error
     */
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
                apptLabel.setPadding(new Insets(3));
                Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 13);
                apptLabel.setFont(font);
                dayList.getChildren().add(apptLabel);
            }
        }
    }

    /**
     * Clears the week UI.
     */
    private void clearWeekAppts() {
        for (int i=0;i<weekApptNodes.length;i++) {
            Pane dayPane = (Pane) weekApptNodes[i];
            VBox dayList = (VBox) dayPane.getChildren().get(0);
            dayList.getChildren().clear();
        }
    }

    /**
     * Clears the calendar UI.
     */
    private void clearMonthAppts() {
        for (int i=1;i<=monthNodes.size();i++) {
            VBox dayList = (VBox) monthNodes.get(i);
            dayList.getChildren().remove(1);
        }
    }

    /**
     * Updates the customers TableView.
     *
     * @exception SQLException db error
     */
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

    /**
     * Handles selecting a customer and enables the update and delete customer buttons.
     */
    private void customerSelect() {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();
            updateCustomerBtn.setDisable(false);
            deleteCustomerBtn.setDisable(false);
        }
    }

    /**
     * Updates the Appointments TableView. Lambda expression used to loop
     * through list of appts and add them to table more efficiently.
     *
     * @exception SQLException db error
     */
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

    /**
     * Handles selecting an appointment and enables the update and delete appointment buttons.
     */
    public void apptSelect() {
        if (apptTable.getSelectionModel().getSelectedItem() != null) {
            selectedAppt = (Appointment) apptTable.getSelectionModel().getSelectedItem();
            updateApptBtn.setDisable(false);
            deleteApptBtn.setDisable(false);
        }
    }

    /**
     * Handles creating the calendar view.
     */
    private void buildCalendar() {
        calendarNodes = new Node[calendarWidth][calendarHeight];
        for (Node child : calendarGrid.getChildren()) {
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            child.setOnMouseClicked((event) -> {
                GridPane grid = (GridPane) child.getParent();
                int i = 0;
                int index = 0;
                LocalDate daySelected = firstOfMonth;
                while (i < 42) {
                    Pane selectedPane = (Pane) grid.getChildren().get(i);
                    if (selectedPane == child) {
                        index = i;
                        VBox dayVbox = (VBox) selectedPane.getChildren().get(0);
                        Label dayLabel = (Label) dayVbox.getChildren().get(0);
                        if (!dayLabel.getText().equals("")) {
                            fillDayAppointmentsMonth(monthAccordionApptList, Integer.valueOf(dayLabel.getText()));
                        }
                        selectedPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
                        selectedPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
                        //daySelected = firstOfMonth.plusDays(i);
                    } else {
                        selectedPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        selectedPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                    i += 1;
                }
            });
            calendarNodes[column][row] = child;
        }
    }

    /**
     * Handles creating the week view.
     */
    private void buildWeek() {
        weekDayNodes = new Node[calendarWidth];
        weekApptNodes = new Node[calendarWidth];
        for (Node child : weekGrid.getChildren()) {
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
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                            dayPane.setBorder(new Border(new BorderStroke(Color.BLUE,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                            daySelected = firstDayOfWeek.plusDays(i);
                        } else {
                            selectedPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            dayPane.setBorder(new Border(new BorderStroke(Color.GREY,
                                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        }
                        i += 1;
                    }
                    fillDayAppointmentsWeek(weekAccordionApptList, index);
                });
                weekApptNodes[column] = child;
            }
        }
    }

    /**
     * Handles updating the accordion list of the selected day of month.
     *
     * @param list javafx accordion component
     * @param index pane selected in month view
     */
    private void fillDayAppointmentsMonth(Accordion list, int index) {
        list.getPanes().clear();
        List<String[]> appts = monthData.get(index-1);
        if (appts.isEmpty()) {
            noApptsLabelMonth.setOpacity(1);
            list.setOpacity(0);
        } else {
            for (String[] appt : appts) {
                DateTimeFormatter frmt = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
                ZonedDateTime st = ZonedDateTime.parse(appt[0] + " UTC", frmt).withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime fn = ZonedDateTime.parse(appt[6] + " UTC", frmt).withZoneSameInstant(ZoneId.systemDefault());
                String start = st.format(DateTimeFormatter.ofPattern("hh:mm a z"));
                String finish = fn.format(DateTimeFormatter.ofPattern("hh:mm a z"));
                GridPane grid = new GridPane();
                grid.setVgap(4);
                grid.setPadding(new Insets(5, 5, 5, 5));
                grid.add(new Label("Appointment ID:  " + appt[1]), 0, 0);
                grid.add(new Label("Description:  " + appt[3]), 0, 1);
                grid.add(new Label("Location:  " + appt[4]), 0, 2);
                grid.add(new Label("Type:  " + appt[5]), 0, 3);
                grid.add(new Label("Start:  " + start), 0, 4);
                grid.add(new Label("End:  " + finish), 0, 5);
                grid.add(new Label("Customer:  " + appt[7]), 0, 6);
                grid.add(new Label("Customer ID:  " + appt[10]), 0, 7);
                grid.add(new Label("Contact:  " + appt[8]), 0, 8);
                grid.add(new Label("Contact ID:  " + appt[11]), 0, 9);
                //Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 14);
                TitledPane titledPane = new TitledPane(appt[2], grid);
                list.getPanes().add(titledPane);
            }
            noApptsLabelMonth.setOpacity(0);
            list.setOpacity(1);
        }
    }
    /**
     * Handles updating the accordion list of appointments of the selected day of week.
     *
     * @param list javafx accordion component
     * @param index pane selected in week view
     */
    private void fillDayAppointmentsWeek(Accordion list, int index) {
        list.getPanes().clear();
        List<String[]> appts = weekData.get(index);
        if (appts.isEmpty()) {
            noApptsLabelWeek.setOpacity(1);
            list.setOpacity(0);
        } else {
            for (String[] appt : appts) {
                DateTimeFormatter frmt = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
                ZonedDateTime st = ZonedDateTime.parse(appt[0] + " UTC", frmt).withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime fn = ZonedDateTime.parse(appt[6] + " UTC", frmt).withZoneSameInstant(ZoneId.systemDefault());
                String start = st.format(DateTimeFormatter.ofPattern("hh:mm a z"));
                String finish = fn.format(DateTimeFormatter.ofPattern("hh:mm a z"));
                GridPane grid = new GridPane();
                grid.setVgap(4);
                grid.setPadding(new Insets(5, 5, 5, 5));
                grid.add(new Label("Appointment ID:  " + appt[1]), 0, 0);
                grid.add(new Label("Description:  " + appt[3]), 0, 1);
                grid.add(new Label("Location:  " + appt[4]), 0, 2);
                grid.add(new Label("Type:  " + appt[5]), 0, 3);
                grid.add(new Label("Start:  " + start), 0, 4);
                grid.add(new Label("End:  " + finish), 0, 5);
                grid.add(new Label("Customer:  " + appt[7]), 0, 6);
                grid.add(new Label("Customer ID:  " + appt[10]), 0, 7);
                grid.add(new Label("Contact:  " + appt[8]), 0, 8);
                grid.add(new Label("Contact ID:  " + appt[11]), 0, 9);
                //Font font = Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 14);
                TitledPane titledPane = new TitledPane(appt[2], grid);
                list.getPanes().add(titledPane);
            }
            noApptsLabelWeek.setOpacity(0);
            list.setOpacity(1);
        }
    }

    /**
     * Handles updating the dates on the week view UI
     *
     * @param day current day of week
     */
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

    /**
     * Handles updating the month dates on the calendar view UI.
     *
     * @param LocalDate firstOfMonth - first day of month
     */
    public void updateCalendarDates(LocalDate firstOfMonth) {
        Map<Integer, VBox> mn = new HashMap<Integer, VBox>();
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
                    mn.put(dayCount,dayVbox);
                    day.setText(String.valueOf(dayCount));
                    dayCount++;
                } else {
                    day.setText("");
                }
            }
        }
        monthNodes = mn;
        String monthName = firstOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
        if (monthYearLabel == null) {
            monthYearLabel = (Label) Main.main_scene.lookup("#monthYearLabel");
        }
        monthYearLabel.setText(monthName + " " + currentYear);
    }

    /**
     * Handles opening the Create Customer UI.
     *
     * @param ActionEvent actionEvent
     */
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

    /**
     * Handles opening the Update Customer UI.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     * @exception IOException
     */
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

    /**
     * Handles selecting a customer and enables the update and delete customer buttons.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     */
    public void handleDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<String, String> editingAppt = db.getCustomerById(selectedCustomer.getId());
        boolean deleted = db.deleteCustomerById(selectedCustomer.getId());
        if (deleted) {
            updateCustomersTable();
            updateApptsTable();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Customer successfully deleted.");
            ((Stage) a.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
            a.show();
        }
    }

    /**
     * Handles opening the Create Appointment UI.
     *
     * @param ActionEvent actionEvent
     */
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

    /**
     * Handles opening the Update Appointment UI.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     * @exception IOException
     */
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

    /**
     * Handles deleting a selected appointment.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     */
    public void handleDeleteAppt(ActionEvent actionEvent) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<String, String> editingAppt = db.getApptById(selectedAppt.getId());
        boolean deleted = db.deleteApptById(selectedAppt.getId());
        if (deleted) {
            updateApptsTable();
        }
    }

    /**
     * Handles changing the week view to the following week.
     *
     * @param ActionEvent actionEvent
     */
    public void handleGoToNextWeek(ActionEvent actionEvent) throws SQLException {
        firstDayOfWeek = lastDayOfWeek.plusDays(2);
        clearWeekAppts();
        updateWeekDates(firstDayOfWeek);
        fillWeekCalendar(firstDayOfWeek, lastDayOfWeek);
    }

    /**
     * Handles changing the week view to the previous week.
     *
     * @param ActionEvent actionEvent
     */
    public void handleGoToPreviousWeek(ActionEvent actionEvent) throws SQLException {
        lastDayOfWeek = firstDayOfWeek.minusDays(2);
        clearWeekAppts();
        updateWeekDates(lastDayOfWeek);
        fillWeekCalendar(firstDayOfWeek, lastDayOfWeek);
    }

    /**
     * Handles changing the calendar view to the following month.
     *
     * @param ActionEvent actionEvent
     */
    public void handleGoToNextMonth(ActionEvent actionEvent) throws SQLException {
        currentMonth += 1;
        firstOfMonth = firstOfMonth.plusMonths(1);
        lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1);
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear += 1;
        }
        clearMonthAppts();
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        updateCalendarDates(date);
        fillMonthCalendar(firstOfMonth,lastOfMonth);
    }

    /**
     * Handles changing the calendar view to the previous month.
     *
     * @param ActionEvent actionEvent
     */
    public void handleGoToPreviousMonth(ActionEvent actionEvent) throws SQLException {
        currentMonth -= 1;
        firstOfMonth = firstOfMonth.minusMonths(1);
        lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1);
        if (currentMonth == 0) {
            currentMonth = 12;
            currentYear -= 1;
        }
        clearMonthAppts();
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        updateCalendarDates(date);
        fillMonthCalendar(firstOfMonth,lastOfMonth);
    }

    /**
     * Sets the logged in user variables as hidden labels.
     */
    public void setUser(String id, String user) {
        hiddenUserIdLabel.setText(id);
        hiddenUsernameLabel.setText(user);
    }

    /**
     * Handles creating the customer appointment report.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     */
    public void getCustomerApptReport(ActionEvent actionEvent) throws SQLException {
        reportsTextField.setOpacity(1);
        reportsTextField.setText("");
        reportsTextField.setEditable(false);

        SqlDriver db = new SqlDriver();
        List<String> values = db.getCustomerApptReport();
        String text = "";
        for (String value : values) {
            text += value + "\n";
        }
        reportsTextField.setText(text);
    }

    /**
     * Handles creating the contact schedule report.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     */
    public void getContactScheduleReport(ActionEvent actionEvent) throws SQLException {
        reportsTextField.setOpacity(1);
        reportsTextField.setText("");
        reportsTextField.setEditable(false);

        SqlDriver db = new SqlDriver();
        List<String> values = db.getContactScheduleReport();
        String text = "";
        for (String value : values) {
            text += value + "\n";
        }
        reportsTextField.setText(text);
    }

    /**
     * Handles creating the customer schedule report.
     *
     * @param ActionEvent actionEvent
     * @exception SQLException db error
     */
    public void getCustomerScheduleBtn(ActionEvent actionEvent) throws SQLException {
        reportsTextField.setOpacity(1);
        reportsTextField.setText("");
        reportsTextField.setEditable(false);

        SqlDriver db = new SqlDriver();
        List<String> values = db.getCustomerScheduleReport();
        String text = "";
        for (String value : values) {
            text += value + "\n";
        }
        reportsTextField.setText(text);
    }
}






