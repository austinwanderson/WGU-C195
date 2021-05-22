package c195.Models;

public class Customer {

    private int customerId; //Auto incremented in database
    private String customerName;
    private String address;
    private String address2;
    private String city;
    private String postalCode;
    private String phone;
    private String country;


    public Customer(int customerId, String customerName, String address, String address2, String city, String postalCode,
                    String phone, String country) {
        setId(customerId);
        setName(customerName);
        setAddress(address);
        setAddress2(address2);
        setCity(city);
        setPostalCode(postalCode);
        setPhone(phone);
        setCountry(country);
    }

    public Customer(int customerId, String customerName) {
        setId(customerId);
        setName(customerName);
    }

    public Customer() {
    }

    public int getId() {
        return customerId;
    }

    public String getName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public void setId(int customerId) {

        this.customerId = customerId;
    }

    public void setName(String customerName) {
        this.customerName = customerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
