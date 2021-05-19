package c195;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Controller<calendarWidth> {

    @FXML private GridPane weekGrid;
    @FXML private Label apptHeader;
    @FXML private TextField apptTitleField;
    @FXML private TextArea apptDescField;
    @FXML private TextField apptContactField;
    @FXML private TextField apptLocationField;
    @FXML private DatePicker apptStartDateField;
    @FXML private ChoiceBox apptStartTimeSelect;
    @FXML private DatePicker apptFinishDateField;
    @FXML private ChoiceBox apptFinishTimeSelect;
    @FXML private ChoiceBox apptCustomerSelect;
    @FXML private Button apptOkBtn;
    @FXML private Button apptCancelBtn;
    @FXML private Label customerHeader;
    @FXML private TextField custNameField;
    @FXML private TextField custPostalCodeField;
    @FXML private ChoiceBox custCountryField;
    @FXML private Button custOkBtn;
    @FXML private Button custCancelBtn;
    @FXML private TextField custAddressField;
    @FXML private TextField custCityField;
    @FXML private ChoiceBox custStateField;
    @FXML private TextField custPhoneField;
    @FXML private Label weekSundayDayLabel;
    @FXML private Label weekMondayDayLabel;
    @FXML private Label weekTuesdayDayLabel;
    @FXML private Label weekWednesdayDayLabel;
    @FXML private Label weekThursdayDayLabel;
    @FXML private Label weekFridayDayLabel;
    @FXML private Label weekSaturdayDayLabel;
    @FXML private Accordion weekAccordionApptList;
    @FXML private Label weekToWeekLabel;
    @FXML private Button nextWeekBtn;
    @FXML private Button previousWeekBtn;
    @FXML private Accordion monthAccordionApptList;
    @FXML private Button addNewCustomerBtn;
    @FXML private Button updateCustomerBtn;
    @FXML private Button deleteCustomerBtn;
    @FXML private Button createApptBtn;
    @FXML private Button updateApptBtn;
    @FXML private Button deleteApptBtn;
    @FXML private TableView apptTable;
    @FXML private TableView customerTable;
    @FXML private Label monthYearLabel;
    @FXML private Button nextMonthBtn;
    @FXML private Button previousMonthBtn;
    @FXML private GridPane calendarGrid;
    @FXML private Label LoginErrorMessage;
    @FXML private Label loginMainLabel;
    @FXML private Label loginPasswordLabel;
    @FXML private Label loginUsernameLabel;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginUsernameField;
    @FXML private Button LoginButton;
    @FXML private Label loginLanguageLabel;
    @FXML private ChoiceBox languageChoiceSelect;

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

    public static Scene main;

    public void handleLoginClicked(ActionEvent actionEvent) throws IOException {
        boolean loggedin = verifyLogin(loginUsernameField.getText(), loginPasswordField.getText());
        if (loggedin) {
            main = new Scene(Main.main_app);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(main);
            window.setTitle("Scheduler");
            window.show();
            initializeUI();
        }
    }

    public boolean verifyLogin(String username, String password) {
        System.out.println(username);
        System.out.println(password);
        return true;
    }

    public void initializeUI() {
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentDay = today.getDayOfMonth();
        currentYear = today.getYear();
        LocalDate firstOfMonth = LocalDate.of(today.getYear(),today.getMonthValue(),1);

        buildCalendar();
        updateCalendarDates(firstOfMonth);
        buildWeek();
        updateWeekDates(today);
    }

    private void buildCalendar() {
        GridPane calendar = (GridPane) main.lookup("#calendarGrid");
        calendarNodes = new Node[calendarWidth][calendarHeight];
        for (Node child : calendar.getChildren()) {
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            calendarNodes[column][row] = child;
        }
    }

    private void buildWeek() {
        GridPane weekCalendar = (GridPane) main.lookup("#weekGrid");
        weekDayNodes = new Node[calendarWidth];
        weekApptNodes = new Node[calendarWidth];
        for (Node child : weekCalendar.getChildren()) {
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            if (row == 0) { weekDayNodes[column] = child; }
            if (row == 1) { weekApptNodes[column] = child; }
        }
    }

    private void updateWeekDates(LocalDate day) {
        System.out.println(day.toString());
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
            weekToWeekLabel = (Label) main.lookup("#weekToWeekLabel");
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
            monthYearLabel = (Label) main.lookup("#monthYearLabel");
        }
        monthYearLabel.setText(monthName + " " + currentYear);
    }

    public void handleAddNewCustomer(ActionEvent actionEvent) throws IOException {
        Parent main_parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer.fxml")));
        Scene main = new Scene(main_parent);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(main);
        window.setTitle("Add Customer");
        window.show();
    }

    public void handleUpdateCustomer(ActionEvent actionEvent) {
    }

    public void handleDeleteCustomer(ActionEvent actionEvent) {
    }

    public void handleCreateNewAppt(ActionEvent actionEvent) throws IOException {
        Parent main_parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("appointment.fxml")));
        Scene main = new Scene(main_parent);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(main);
        window.setTitle("Create Appointment");
        window.show();
    }

    public void handleUpdateAppt(ActionEvent actionEvent) {
    }

    public void handleDeleteAppt(ActionEvent actionEvent) {
    }

    public void handleGoToNextWeek(ActionEvent actionEvent) {
        firstDayOfWeek = lastDayOfWeek.plusDays(2);
        updateWeekDates(firstDayOfWeek);
    }

    public void handleGoToPreviousWeek(ActionEvent actionEvent) {
        lastDayOfWeek = firstDayOfWeek.minusDays(2);
        updateWeekDates(lastDayOfWeek);
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

    public void handleApptOk(ActionEvent actionEvent) {
    }

    public void handleApptCancel(ActionEvent actionEvent) {
    }

    public void handleCustomerOk(ActionEvent actionEvent) {
    }

    public void handleCustomerCancel(ActionEvent actionEvent) {
    }

}
