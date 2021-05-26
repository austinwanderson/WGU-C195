package c195;

import c195.Models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SqlDriver {

    private Connection db;

    public SqlDriver() {
        try {
            db = DriverManager.getConnection("jdbc:mysql://wgudb.ucertify.com:3306/WJ07wiZ","U07wiZ", "53689153276");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet getUserCredentials(String username, String password) {
        Statement loginStatement = null;
        ResultSet credentials = null;
        try {
            loginStatement = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            credentials = loginStatement.executeQuery("select * from users where User_ID = '" + username + "' and Password = '" + password + "';");
            return credentials;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public int createAppointment(Appointment appointment) {
        Statement createApptStatement = null;
        String now = Instant.now().atZone(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int newAppt;
        try {
            String query = "insert into appointments (title, description, location, type, start, end, " +
                    "create_date, created_by, last_update, last_updated_by, customer_id, user_id, contact_id) values ('" + appointment.getTitle() +
                    "','" + appointment.getDescription() + "','" + appointment.getLocation() + "','" + appointment.getType() + "','" +
                    appointment.getStartDateTime() + "','" + appointment.getEndDateTime() + "','" + now + "','1','" + now + "','1','2','3','4');";
            createApptStatement = this.db.createStatement();
            newAppt = createApptStatement.executeUpdate(query);
            return newAppt;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public ObservableList<String[]> getCustomerNames() throws SQLException {
        Statement customerQuery = null;
        ResultSet results = null;
        ObservableList<String[]> names = FXCollections.observableArrayList();
        customerQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = customerQuery.executeQuery("select customer_id, customer_name from customers;");
        while (results.next()) {
            String[] n = {results.getString("customer_id"), results.getString("customer_name")};
            names.add(n);
        }
        return names;
    }

    public Boolean checkValidApptTime(String customerId, ZonedDateTime start, ZonedDateTime finish) throws SQLException {
        String st = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ft = finish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Statement apptQuery = null;
        ResultSet results = null;
        String query = "select * from appointments where customer_id = '" + customerId + "' and " +
                "start between '" + st + "' and '" + ft + "' and end between '" + st + "' and '" + ft + "';";
        apptQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptQuery.executeQuery(query);
        while (results.next()) {
            return false;
        }
        return true;
    }
}












