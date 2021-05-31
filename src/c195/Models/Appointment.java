package c195.Models;

import c195.SqlDriver;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Appointment {

    private String appointmentId;
    private String customerId;
    private String contactId;
    private String createdById;
    private String updatedById;
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String startDate;
    private String startTime;
    private String finishDate;
    private String finishTime;
    private String start;
    private String end;
    private ZonedDateTime startUTC;
    private ZonedDateTime finishUTC;

    public Appointment(String title, String description, String type, String contactId, String location, String startDate, String startTime,
                       String finishDate, String finishTime, String customerId, String userId){
        setCustomerId(customerId);
        setContactId(contactId);
        setCreatedById(userId);
        setUpdatedById(userId);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setAppointmentStart(startDate, startTime);
        setAppointmentEnd(finishDate, finishTime);
    }

    public Appointment(String id, String title, String start, String end, String contact) {
        setAppointmentId(id);
        setTitle(title);
        setStart(start);
        setEnd(end);
        setContact(contact);
    }

    public String getId(){
        return this.appointmentId;
    }
    public String getTitle(){
        return this.title;
    }
    public String getDescription(){
        return this.description;
    }
    public String getLocation(){
        return this.location;
    }
    public String getContactId(){
        return this.contactId;
    }
    public String getCreatedById(){ return this.createdById; }
    public String getUpdatedById(){ return this.updatedById; }
    public String getCustomerId(){ return this.customerId; }
    public String getType(){
        return this.type;
    }
    public String getStart() {
        String d = this.start + " UTC" ;
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
        ZonedDateTime e = ZonedDateTime.parse(d,f);
        ZonedDateTime g = e.withZoneSameInstant(ZoneId.systemDefault());
        return g.format(f);
    }
    public String getEnd() {
        String d = this.end + " UTC";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss z");
        ZonedDateTime e = ZonedDateTime.parse(d,f);
        ZonedDateTime g = e.withZoneSameInstant(ZoneId.systemDefault());
        return g.format(f);
    }
    public String getContact() { return this.contact; }
    public Customer getCustomer() {
        return customer;
    }

    public String getStartDateTime() {
        return this.startUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getEndDateTime() {
        return this.finishUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ZonedDateTime getUTCStartDate() {
        return this.startUTC;
    }

    public ZonedDateTime getUTCFinishDate() {
        return this.finishUTC;
    }

    private void setCustomer(Customer customer){
        this.customer = customer;
    }
    private void setAppointmentId(String appointmentId){
        this.appointmentId = appointmentId;
    }
    private void setCustomerId(String customerId){
        this.customerId = customerId;
    }
    private void setCreatedById(String userId){
        this.createdById = userId;
    }
    private void setUpdatedById(String userId){
        this.updatedById = userId;
    }
    private void setContactId(String contactId){
        this.contactId = contactId;
    }
    private void setTitle(String title){
        this.title = title;
    }
    private void setDescription(String description){
        this.description = description;
    }
    private void setLocation(String location){
        this.location = location;
    }
    private void setStart(String start){
        this.start = start;
    }
    private void setEnd(String end){
        this.end = end;
    }
    private void setContact(String contact){ this.contact = contact; }
    private void setType(String type){ this.type = type; }

    private void setAppointmentStart(String startDate, String startTime){
        this.startTime = startTime;
        this.startDate = startDate;
        String dt = (startDate + " " + startTime.substring(0,12));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a z");
        ZonedDateTime estdate = ZonedDateTime.parse(dt, f);
        this.startUTC = estdate.withZoneSameInstant(ZoneId.of("+0"));
    }

    private void setAppointmentEnd(String finishDate, String finishTime){
        this.finishDate = finishDate;
        this.finishTime = finishTime;
        String dt = (finishDate + " " + finishTime.substring(0,12));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a z");
        ZonedDateTime estdate = ZonedDateTime.parse(dt, f);
        this.finishUTC = estdate.withZoneSameInstant(ZoneId.of("+0"));
    }

    public boolean pushToDatabase() {
        SqlDriver db = new SqlDriver();
        int appointmentId = db.createAppointment(this);
        if (appointmentId > 0) {
            return true;
        }
        return false;
    }

    public boolean updateApptById(String apptId) {
        SqlDriver db = new SqlDriver();
        return db.updateAppointment(this, apptId);
    }
}






