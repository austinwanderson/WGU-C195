package c195.Models;

import c195.SqlDriver;

import java.sql.SQLException;

/**
 * Customer model for all customer functionality.
 *
 *
 * @author  Austin Anderson
 * @version 1.0
 * @since   2021-06-10
 */
public class Customer {

    private String customer_id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private String division_id;
    private String division;
    private String country;
    private String updated_by;

    public Customer() {
    }

    /**
     * Customer constructor
     *
     * @param custName
     * @param custAddr
     * @param custDivisionId
     * @param custZip
     * @param custPhone
     * @param userId
     */
    public Customer(String custName, String custAddr, String custDivisionId, String custZip, String custPhone, String userId) {
        setName(custName);
        setAddress(custAddr);
        setDivisionId(custDivisionId);
        setPostalCode(custZip);
        setPhone(custPhone);
        setUpdatedBy(userId);
    }

    /**
     * Customer constructor
     *
     * @param custName
     * @param custAddr
     * @param custDivisionId
     * @param custZip
     * @param phone
     * @param custId
     * @param userId
     * @param forTable
     */
    public Customer(String custId, String custName, String custAddr, String custPostalCode, String custCountry, String custDivision, String phone, Boolean forTable) {
        setId(custId);
        setName(custName);
        setAddress(custAddr);
        setPostalCode(custPostalCode);
        setCountry(custCountry);
        setDivision(custDivision);
        setPhone(phone);
    }

    public String getId() {
        return this.customer_id;
    }
    public String getUpdatedBy() { return this.updated_by; }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        if (this.division != null) {
            return this.address + ", " + this.division + ", " + this.country + " " + this.postalCode;
        } else {
            return this.address;
        }
    }
    public String getPostalCode() {
        return this.postalCode;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getDivisionId() {
        return this.division_id;
    }

    public void setId(String customerId) { this.customer_id = customerId; }
    public void setUpdatedBy(String userId) { this.updated_by = userId; }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCountry(String country) { this.country = country; }
    public void setDivision(String division) { this.division = division; }
    public void setDivisionId(String division_id) {
        this.division_id = division_id;
    }

    /**
     * Pushes customer to database
     *
     * @return Boolean true if successful
     */
    public Boolean pushToDatabase() {
        SqlDriver db = new SqlDriver();
        int appointmentId = db.createCustomer(this);
        if (appointmentId > 0) {
            return true;
        }
        return false;
    }

    /**
     * updates customer by ID
     *
     * @param customerId
     * @return Boolean true if successfully updated.
     */
    public Boolean updateCustomerById(String customerId) {
        SqlDriver db = new SqlDriver();
        return db.updateCustomer(this, customerId);
    }
}
