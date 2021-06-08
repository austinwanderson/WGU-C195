package c195;

import c195.Models.Appointment;
import c195.Models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
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
        Statement apptQuery = null;
        ResultSet results = null;
        String query = "select * from appointments where customer_id = '" + customerId + "' and " +
                "start between '" + st + "' and '" + ft + "' and end between '" + st + "' and '" + ft + "';";
        apptQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = apptQuery.executeQuery(query);
        while (results.next()) {
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

    public Map<String, String> getCustomerById(String id) throws SQLException {
        Statement custQuery = null;
        ResultSet results = null;
        custQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = custQuery.executeQuery("select * from customers where customer_id = '" + id + "';");
        while (results.next()) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("customer_id", results.getString("customer_id"));
            data.put("customer_name", results.getString("customer_name"));
            data.put("address", results.getString("address"));
            data.put("postal_code", results.getString("postal_code"));
            data.put("phone", results.getString("phone"));
            data.put("division_id", results.getString("division_id"));
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

    public ObservableList<String[]> getCountries() throws SQLException {
        Statement countryQuery = null;
        ResultSet results = null;
        ObservableList<String[]> countries = FXCollections.observableArrayList();
        countryQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = countryQuery.executeQuery("select country_id, country from countries;");
        while (results.next()) {
            String[] n = {results.getString("country_id"), results.getString("country")};
            countries.add(n);
        }
        return countries;
    }

    public ObservableList<String[]> getStatesProvinces(String countryId) throws SQLException {
        Statement stateQuery = null;
        ResultSet results = null;
        ObservableList<String[]> divisions = FXCollections.observableArrayList();
        stateQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = stateQuery.executeQuery("select division, division_id, country_id from first_level_divisions where country_id = '" + countryId + "' order by division;");
        while (results.next()) {
            String[] n = {results.getString("division"), results.getString("division_id"), results.getString("country_id")};
            divisions.add(n);
        }
        return divisions;
    }

    public int createCustomer(Customer customer) {
        Statement createCustomerStatement = null;
        String now = Instant.now().atZone(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int newCustomerId;
        try {
            String query = "insert into customers (customer_name, address, division_id, postal_code, phone, create_date, " +
                    "created_by, last_update, last_updated_by) values ('" + customer.getName() +
                    "','" + customer.getAddress() + "','" + customer.getDivisionId() + "','" + customer.getPostalCode() + "','" +
                    customer.getPhone() + "','" + now + "','" + customer.getUpdatedBy() + "','" + now +
                    "','" + customer.getUpdatedBy() +"');";
            createCustomerStatement = this.db.createStatement();
            newCustomerId = createCustomerStatement.executeUpdate(query);
            return newCustomerId;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public List<String[]> getCustomersForTable() throws SQLException {
        List<String[]> customers = new ArrayList<String[]>();
        Statement customersQuery = null;
        ResultSet results = null;
        Map<String, String[]> cd = new HashMap<String, String[]>();
        ObservableList<String[]> countries = this.getCountries();
        for (String[] country : countries) {
            ObservableList<String[]> divisions = this.getStatesProvinces(country[0]);
            for (String[] division : divisions) {
                cd.put(division[1], new String[]{country[1], division[0]});
            }
        }
        customersQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        results = customersQuery.executeQuery("select customer_id, customer_name, address, postal_code, division_id, phone from customers;");
        int i = 0;
        while (results.next()) {
            customers.add(i, new String[]{results.getString("customer_id"), results.getString("customer_name"), results.getString("address"),
                    results.getString("postal_code"), cd.get(results.getString("division_id"))[0], cd.get(results.getString("division_id"))[1],
                    results.getString("phone")});
            i += 1;
        }
        return customers;
    }

    public Boolean updateCustomer(Customer customer, String customerId) {
        Statement updateCustomerStatement = null;
        String now = Instant.now().atZone(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int newCustomer;
        try {
            String query = "update customers set customer_name = '" + customer.getName() + "', address = '" + customer.getAddress() +
                    "', phone = '" + customer.getPhone() + "', division_id = '" + customer.getDivisionId() + "', postal_code = '" + customer.getPostalCode() +
                    "', last_update = '" + now + "', last_updated_by = '" + customer.getUpdatedBy() + "' where customer_id = '" + customerId + "';";

            updateCustomerStatement = this.db.createStatement();
            newCustomer = updateCustomerStatement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomerById(String id) {
        Statement deleteStatement = null;
        int deleted;
        try {
            String query = "delete from appointments where customer_id = '" + id + "';";
            deleteStatement = this.db.createStatement();
            deleted = deleteStatement.executeUpdate(query);

            query = "delete from customers where customer_id = '" + id + "';";
            deleteStatement = this.db.createStatement();
            deleted = deleteStatement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public Map<Integer, List<String[]>> getApptsByWeek(LocalDate f, LocalDate l) throws SQLException {
        List<String[]> appointments = new ArrayList<String[]>();
        Map<Integer, List<String[]>> data = new HashMap<Integer, List<String[]>>();
        Statement apptsQuery = null;
        ResultSet results = null;
        apptsQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "select * from appointments inner join customers on appointments.customer_id" +
                " = customers.customer_id inner join contacts on appointments.contact_id = contacts.contact_id" +
                " where start between '" + f + " 00:00:00" + "' and '" + l + " 23:59:59" + "' order by start asc;";
        results = apptsQuery.executeQuery(query);
        int i = 0;
        int j = 0;
        LocalDate currentDay = f;
        while (results.next()) {
            LocalDate startDay = LocalDate.parse(results.getString("start").substring(0,10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (startDay.equals(currentDay)) {
                appointments.add(i,new String[]{results.getString("start"),results.getString("appointment_id"), results.getString("title"),
                        results.getString("description"),results.getString("location"),results.getString("type"),results.getString("end"),
                        results.getString("customer_name"),results.getString("contact_name"),results.getString("email"),results.getString("customer_id"),
                        results.getString("contact_id")});
                i += 1;
            } else {
                data.put(j, appointments);
                currentDay = currentDay.plusDays(1);
                appointments = new ArrayList<String[]>();
                results.previous();
                i = 0;
                j += 1;
            }
        }
        data.put(j, appointments);
        int k = 0;
        while (k < 7) {
            if (!data.containsKey(k)) {
                data.put(k, new ArrayList<String[]>());
            }
            k += 1;
        }

        return data;
    }

    public Map<Integer, List<String[]>> getApptsByMonth(LocalDate f, LocalDate l, int lengthOfMonth) throws SQLException {
        List<String[]> appointments = new ArrayList<String[]>();
        Map<Integer, List<String[]>> data = new HashMap<Integer, List<String[]>>();
        Statement apptsQuery = null;
        ResultSet results = null;
        apptsQuery = this.db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "select * from appointments inner join customers on appointments.customer_id" +
                " = customers.customer_id inner join contacts on appointments.contact_id = contacts.contact_id" +
                " where start between '" + f + " 00:00:00" + "' and '" + l + " 23:59:59" + "' order by start asc;";
        results = apptsQuery.executeQuery(query);
        int i = 0;
        int j = 0;
        LocalDate currentDay = f;
        while (results.next()) {
            LocalDate startDay = LocalDate.parse(results.getString("start").substring(0,10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (startDay.equals(currentDay)) {
                appointments.add(i,new String[]{results.getString("start"),results.getString("appointment_id"), results.getString("title"),
                        results.getString("description"),results.getString("location"),results.getString("type"),results.getString("end"),
                        results.getString("customer_name"),results.getString("contact_name"),results.getString("email"),results.getString("customer_id"),
                        results.getString("contact_id")});
                i += 1;
            } else {
                data.put(j, appointments);
                currentDay = currentDay.plusDays(1);
                appointments = new ArrayList<String[]>();
                results.previous();
                i = 0;
                j += 1;
            }
        }
        data.put(j, appointments);
        int k = 0;
        while (k < lengthOfMonth) {
            if (!data.containsKey(k)) {
                data.put(k, new ArrayList<String[]>());
            }
            k += 1;
        }

        return data;
    }
}












