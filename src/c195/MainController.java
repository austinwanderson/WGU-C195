package c195;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class MainController {

    @FXML public AnchorPane ap;
    @FXML public GridPane weekGrid;
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

    Parent root;
    Stage stage;

    @FXML
    public void initialize() throws Exception {
        initializeUI();
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
            Integer column = GridPane.getColumnIndex(child);
            Integer row = GridPane.getRowIndex(child);
            if (column == null) { column = 0; }
            if (row == null) { row = 0; }
            if (row == 0) { weekDayNodes[column] = child; }
            if (row == 1) { weekApptNodes[column] = child; }
        }
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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer.fxml")));
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add Customer");
        stage.show();
    }

    public void handleUpdateCustomer(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("customer.fxml")));
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Update Customer");
        stage.show();
    }

    public void handleDeleteCustomer(ActionEvent actionEvent) {
    }

    public void handleCreateNewAppt(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("appointment.fxml")));
        root = loader.load();
        AppointmentController apptCtrl = loader.getController();
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Create Appointment");
        stage.show();
    }

    public void handleUpdateAppt(ActionEvent actionEvent) throws IOException {
        //String str = estdate.format(DateTimeFormatter.ofPattern("hh:mm a")) + " EST (" + localdate.format(DateTimeFormatter.ofPattern("hh:mm a z")) + ")";
        FXMLLoader loader = new FXMLLoader((getClass().getResource("appointment.fxml")));
        root = loader.load();
        AppointmentController apptCtrl = loader.getController();
        apptCtrl.setHeader("Update Appointment");
        apptCtrl.setTitle("This is a test.");
        stage = (Stage)ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Update Appointment");
        stage.show();
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
}
