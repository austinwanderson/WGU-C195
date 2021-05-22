package c195.Models;

import java.sql.Date;

public class Appointment {

    private int appointmentId;
    private int customerId;
    private int consultantId;
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private Date appointmentStart;
    private Date appointmentEnd;
    private Date createDate;


    public Appointment(int appointmentId, int customerId, int consultantId, String title, String description, String location,
                       String contact, String type, Date appointmentStart, Date appointmentEnd, Date createDate){
        setAppointmentId(appointmentId);
        setCustomerId(customerId);
        setConsultantId(consultantId);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contact);
        setType(type);
        setAppointmentStart(appointmentStart);
        setAppointmentEnd(appointmentEnd);
        setCreateDate(createDate);
    }

    public Appointment(Date appointmentStart, Date appointmentEnd, String title, String type) {
        setAppointmentStart(appointmentStart);
        setAppointmentEnd(appointmentEnd);
        setTitle(title);
        setType(type);
    }

    public Appointment(){
    }

    public int getAppointmentId(){
        return this.appointmentId;
    }

    public int getCustomerId(){
        return this.customerId;
    }

    public int getConsultantId(){
        return this.consultantId;
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

    public Date getStart(){
        return this.appointmentStart;
    }

    public Date getEnd(){
        return this.appointmentEnd;
    }

    public Date getCreateDate(){
        return this.createDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    private void setCustomer(Customer customer){
        this.customer = customer;
    }
    private void setAppointmentId(int appointmentId){
        this.appointmentId = appointmentId;
    }

    private void setCustomerId(int customerId){
        this.customerId = customerId;
    }

    private void setConsultantId(int consultantId){
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

    private void setAppointmentStart(Date startTime){
        this.appointmentStart = startTime;
    }

    private void setAppointmentEnd(Date endTime){
        this.appointmentEnd = endTime;
    }

    private void setCreateDate(Date createDate){
        this.createDate = createDate;
    }
}
