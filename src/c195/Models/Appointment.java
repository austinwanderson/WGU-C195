package c195.Models;

import c195.SqlDriver;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Appointment {

    private int appointmentId;
    private String customerId;
    private String consultantId;
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
    private ZonedDateTime startUTC;
    private ZonedDateTime finishUTC;

    //Appointment newAppt = new Appointment(apptTitle, apptDesc, apptType apptContact, apptLocation, apptStartDate, apptStartTime,
    //                apptFinishDate, apptFinishTime, apptCustomer, userId);

    public Appointment(String title, String description, String type, String contact, String location, String startDate, String startTime,
                       String finishDate, String finishTime, String customerId, String consultantId){
        setCustomerId(customerId);
        setConsultantId(consultantId);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contact);
        setType(type);
        setAppointmentStart(startDate, startTime);
        setAppointmentEnd(finishDate, finishTime);
    }

    public Appointment(){
    }

    public int getAppointmentId(){
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

    public String getContact(){
        return this.contact;
    }

    public String getType(){
        return this.type;
    }

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
    private void setAppointmentId(int appointmentId){
        this.appointmentId = appointmentId;
    }

    private void setCustomerId(String customerId){
        this.customerId = customerId;
    }

    private void setConsultantId(String consultantId){
        this.consultantId = consultantId;
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

    private void setContact(String contact){
        this.contact = contact;
    }

    private void setType(String type){
        this.type = type;
    }

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
}






