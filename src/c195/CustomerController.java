package c195;

import c195.Models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * CustomerController handles all events for the customer UI.
 *
 *
 * @author  Austin Anderson
 * @version 1.0
 * @since   2021-06-10
 */
public class CustomerController {

    @FXML public AnchorPane ap;
    @FXML public Label customerHeader;
    @FXML public Label hiddenUserIdLabel;
    @FXML public Label hiddenUsernameLabel;
    @FXML public Label hiddenCustomerIdLabel;
    @FXML public Label hiddenDivisionIdLabel;
    @FXML public TextField custNameField;
    @FXML public TextField custPostalCodeField;
    @FXML public ChoiceBox custCountrySelect;
    @FXML public Button custOkBtn;
    @FXML public Button custCancelBtn;
    @FXML public TextField custAddressField;
    @FXML public ChoiceBox custStateSelect;
    @FXML public TextField custPhoneField;
    @FXML public Label custErrorMessage;
    private ObservableList<String[]> countries;
    private ObservableList<String[]> divisions;

    Parent root;
    Stage stage;

    /**
     * Initializes Customer UI.
     *
     * @exception Exception
     */
    @FXML
    public void initialize() throws Exception {
        populateCountrySelect();
    }

    /**
     * Sets the country value if updating a customer.
     *
     * @param country given country
     */
    public void setCountryValue(String country) {
        ObservableList items = custCountrySelect.getItems();
        Boolean done = false;
        int i = 0;
        Iterator j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString();
            if (value.equals(country)) {
                custCountrySelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }
    }

    /**
     * sets the state value if updating a customer.
     *
     * @param state given state
     */
    public void setStateValue(String state) {
        ObservableList items = custStateSelect.getItems();
        Boolean done = false;
        int i = 0;
        Iterator j = items.iterator();
        while (j.hasNext() && !done) {
            String value = j.next().toString();
            if (value.equals(state)) {
                custStateSelect.getSelectionModel().select(i);
                done = true;
            }
            i += 1;
        }
    }

    /**
     * Populates the country select form item.
     *
     * @exception SQLException db error
     */
    private void populateCountrySelect() throws SQLException {
        SqlDriver db = new SqlDriver();
        ObservableList<String[]> countries = db.getCountries();
        setCountries(countries);
        ObservableList<Object> l = FXCollections.observableArrayList();
        countries.forEach((c) -> {
            l.add(c[1]);
        });
        custCountrySelect.setItems(l);
        custCountrySelect.setOnAction(event -> {
            String selectedCountry = custCountrySelect.getValue().toString();
            try {
                populateStateProvinceSelect(selectedCountry);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    /**
     * Populates the state select form UI based on country selected.
     *
     * @param selectedCountry given country name
     * @exception SQLException db error
     */
    private void populateStateProvinceSelect(String selectedCountry) throws SQLException {
        this.countries.forEach((c) -> {
            if (c[1].equals(selectedCountry)) {
                String countryId = c[0];
                SqlDriver db = new SqlDriver();
                try {
                    ObservableList<String[]> provinces = db.getStatesProvinces(countryId);
                    setDivisions(provinces);
                    ObservableList<Object> l = FXCollections.observableArrayList();
                    provinces.forEach((p) -> {
                        l.add(p[0]);
                    });
                    custStateSelect.setItems(l);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    private void setDivisions(ObservableList<String[]> provinces) {
        this.divisions = provinces;
    }

    private void setCountries(ObservableList<String[]> countries) {
        this.countries = countries;
    }

    public void handleCustomerOk(ActionEvent actionEvent) throws IOException {

        if (hiddenCustomerIdLabel.getText() == "") {
            createNewCustomer();
        } else {
            updateCustomer(hiddenCustomerIdLabel.getText());
        }
    }

    /**
     * Handles creating a new customer.
     *
     * @exception SQLException db error
     */
    private void createNewCustomer() throws IOException {
        String custName = custNameField.getText();
        String custAddr = custAddressField.getText();
        String custZip = custPostalCodeField.getText();
        String custPhone = custPhoneField.getText();
        String custCountry = (custCountrySelect.getValue() != null) ? custCountrySelect.getValue().toString() : "";
        String custState = (custStateSelect.getValue() != null) ? custStateSelect.getValue().toString() : "";
        Boolean validated = validateInput(custName, custAddr, custZip, custPhone, custCountry,
                custState, "");
        if (validated) {
            final String[] custDivisionId = new String[1];
            getDivisions().forEach((division) -> {
                if (custState == division[0]) {
                    custDivisionId[0] = division[1];
                }
            });
            Customer newCustomer = new Customer(custName, custAddr, custDivisionId[0], custZip, custPhone, hiddenUserIdLabel.getText());
            Boolean created = newCustomer.pushToDatabase();
            if (created) {
                loadHomeScreen();
            } else {
                custErrorMessage.setText("There was a DB error.");
                custErrorMessage.setOpacity(1);
            }
        }
    }

    /**
     * Updates a customer.
     *
     * @param customerId customer ID to be updated.
     * @exception IOException db error
     */
    private void updateCustomer(String customerId) throws IOException {
        String custName = custNameField.getText();
        String custAddr = custAddressField.getText();
        String custZip = custPostalCodeField.getText();
        String custPhone = custPhoneField.getText();
        String custCountry = (custCountrySelect.getValue() != null) ? custCountrySelect.getValue().toString() : "";
        String custState = (custStateSelect.getValue() != null) ? custStateSelect.getValue().toString() : "";
        Boolean validated = validateInput(custName, custAddr, custZip, custPhone, custCountry,
                custState, "");
        if (validated) {
            final String[] custDivisionId = new String[1];
            getDivisions().forEach((division) -> {
                if (custState == division[0]) {
                    custDivisionId[0] = division[1];
                }
            });
            Customer updatedCustomer = new Customer(custName, custAddr, custDivisionId[0], custZip, custPhone, hiddenUserIdLabel.getText());
            Boolean created = updatedCustomer.updateCustomerById(customerId);
            if (created) {
                loadHomeScreen();
            } else {
                custErrorMessage.setText("There was a DB error.");
                custErrorMessage.setOpacity(1);
            }
        }
    }

    /**
     * Returns back to the main UI.
     *
     * @exception IOException IO error
     */
    private void loadHomeScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("main.fxml")));
        root = loader.load();
        MainController mainCtrl = loader.getController();
        mainCtrl.setUser(hiddenUserIdLabel.getText(), hiddenUsernameLabel.getText());
        stage = (Stage) ap.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scheduler");
        stage.show();
    }

    public ObservableList<String[]> getDivisions() {
        return divisions;
    }

    /**
     * Validates all form items are filled out.
     *
     * @param custName
     * @param custAddr
     * @param custZip
     * @param custPhone
     * @param custCountry
     * @param custState
     * @param customer_id
     * @return boolean true if items are validated.
     */
    private Boolean validateInput(String custName, String custAddr, String custZip, String custPhone,
                                  String custCountry, String custState, String customer_id) {

        if (custName.length() != 0 && custAddr.length() != 0 && custZip.length() != 0 && custPhone.length() != 0
                && custCountry.length() != 0 && custState.length() != 0) {
            return true;
        } else {
            custErrorMessage.setText("All fields need to be filled out.");
            custErrorMessage.setOpacity(1);
            return false;
        }
    }

    /**
     * Returns back to home screen after customer hits cancel btn
     *
     * @param actionEvent click event
     * @exception IOException IO error
     */
    public void handleCustomerCancel(ActionEvent actionEvent) throws IOException {
        loadHomeScreen();
    }

    /**
     * Sets the hidden label username and user id
     *
     * @param id given user id
     * @param user given username
     */
    public void setUser(String id, String user) {
        hiddenUsernameLabel.setText(user);
        hiddenUserIdLabel.setText(id);
    }

    /**
     * Gets the country and province name selected in the form.
     *
     * @param division_id state/province ID
     * @return String[] state and province name
     * @exception SQLException db error
     */
    public String[] getCountryAndProvince(String division_id) throws SQLException {
        SqlDriver db = new SqlDriver();
        Map<String, String[]> cd = new HashMap<String, String[]>();
        ObservableList<String[]> countries = db.getCountries();
        for (String[] country : countries) {
            ObservableList<String[]> divisions = db.getStatesProvinces(country[0]);
            for (String[] division : divisions) {
                if (division[1].equals(division_id)) {
                    return new String[]{country[1], division[0]};
                }
            }
        }
        return null;
    }















}
