package c195;

import c195.Models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
            credentials = loginStatement.executeQuery("select * from users where User_Name = '" + username + "' and Password = '" + password + "';");
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
                    appointment.getStartDateTime() + "','" + appointment.getEndDateTime() + "','" + now + "','" + appointment.getCreatedById() +
                    "','" + now + "','" + appointment.getUpdatedById() + "','" + appointment.getCustomerId() + "','" + appointment.getCreatedById() +
                    "','" + appointment.getContactId() + "');";
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

    public ObservableList<String[]> getContacts() throws SQLException {
        Statement contactQuery = null;
        ResultSet results = null;
        ObservableList<String[]> contacts = FXCollections.observableArrayList();
        contactQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = contactQuery.executeQuery("select * from contacts;");
        while (results.next()) {
            String[] n = {results.getString("contact_id"), results.getString("contact_name"), results.getString("email")};
            contacts.add(n);
        }
        return contacts;
    }

    public String[] getUser(String userId) throws SQLException {
        Statement userQuery = null;
        ResultSet results = null;
        userQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = userQuery.executeQuery("select * from users where user_id = '" + userId + "' or user_name = '" + userId + "';");
        while (results.next()) {
            String[] n = {results.getString("user_id"), results.getString("user_name"), results.getString("password")};
            return n;
        }
        return new String[] {"","",""};
    }

    public List<String[]> getApptsForTable() throws SQLException {
        List<String[]> appts = new ArrayList<String[]>();
        Statement apptsQuery = null;
        ResultSet results = null;
        apptsQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptsQuery.executeQuery("select appointment_id, title, start, end, contact_id from appointments;");
        int i = 0;
        while (results.next()) {
            appts.add(i, new String[]{results.getString("appointment_id"), results.getString("title"), results.getString("start"),
                    results.getString("end"), results.getString("contact_id")});
            i += 1;
        }
        return appts;
    }

    public String getContactNameById(String id) throws SQLException {
        Statement nameQuery = null;
        ResultSet results = null;
        nameQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = nameQuery.executeQuery("select contact_name from contacts where contact_id = '" + id + "';");
        while (results.next()) {
            return results.getString("contact_name");
        }
        return "";
    }

    public Appointment getApptById(String id) throws SQLException {
        Statement apptQuery = null;
        ResultSet results = null;
        apptQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptQuery.executeQuery("select contact_name from contacts where contact_id = '" + id + "';");
        while (results.next()) {
            //new Appointment(apptTitle, apptDesc, apptType, apptContact, apptLocation, apptDate, apptStartTime,
            //        //                    apptDate, apptFinishTime, customerId[0], userId);
            return new Appointment(results.getString("title"),results.getString("description"),results.getString("contact_id"),
                    results.getString("location"),results.getString("description"),results.getString("start").substring(0,11),
                    results.getString("start").substring(12),results.getString("finish").substring(0,11),results.getString("finish").substring(12),
                    results.getString("customer_id"),results.getString("user_id"));
        }
        return null;
    }
}












