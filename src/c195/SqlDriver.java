package c195;

import c195.Models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public Boolean checkValidApptTime(String customerId, ZonedDateTime start, ZonedDateTime finish, String apptId) throws SQLException {
        String st = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ft = finish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(apptId + " 75");
        Statement apptQuery = null;
        ResultSet results = null;
        String query = "select * from appointments where customer_id = '" + customerId + "' and " +
                "start between '" + st + "' and '" + ft + "' and end between '" + st + "' and '" + ft + "';";
        apptQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptQuery.executeQuery(query);
        while (results.next()) {
            System.out.println(results.getString("appointment_id").equals(apptId) + " 82");
            if (!results.getString("appointment_id").equals(apptId)) {
                return false;
            }
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

    public String getCustomerNameById(String id) throws SQLException {
        Statement nameQuery = null;
        ResultSet results = null;
        nameQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = nameQuery.executeQuery("select customer_name from customers where customer_id = '" + id + "';");
        while (results.next()) {
            return results.getString("customer_name");
        }
        return "";
    }

    public Map<String, String> getApptById(String id) throws SQLException {
        Statement apptQuery = null;
        ResultSet results = null;
        apptQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptQuery.executeQuery("select * from appointments where appointment_id = '" + id + "';");
        while (results.next()) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("end", results.getString("end"));
            data.put("start", results.getString("start"));
            data.put("title", results.getString("title"));
            data.put("description", results.getString("description"));
            data.put("contact_id", results.getString("contact_id"));
            data.put("location", results.getString("location"));
            data.put("type", results.getString("type"));
            data.put("customer_id", results.getString("customer_id"));
            data.put("user_id", results.getString("user_id"));
            data.put("appointment_id", results.getString("appointment_id"));
            return data;
        }
        return null;
    }

    public boolean updateAppointment(Appointment appointment, String apptId) {
        Statement updateApptStatement = null;
        String now = Instant.now().atZone(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int newAppt;
        try {
            String query = "update appointments set title = '" + appointment.getTitle() + "', description = '" + appointment.getDescription() +
                    "', location = '" + appointment.getLocation() + "', type = '" + appointment.getType() + "', start = '" + appointment.getStartDateTime() +
                    "', end = '" + appointment.getEndDateTime() + "', last_update = '" + now + "', last_updated_by = '" + appointment.getCreatedById() +
                    "', customer_id = '" + appointment.getCustomerId() + "', user_id = '" + appointment.getCreatedById() + "', contact_id = '" +
                    appointment.getContactId() + "' where appointment_id = '" + apptId + "';";

            System.out.println(query);

            updateApptStatement = this.db.createStatement();
            newAppt = updateApptStatement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean deleteApptById(String id) {
        Statement deleteStatement = null;
        int deleted;
        try {
            String query = "delete from appointments where appointment_id = '" + id + "';";
            deleteStatement = this.db.createStatement();
            deleted = deleteStatement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}












