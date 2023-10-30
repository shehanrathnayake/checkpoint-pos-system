package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.tm.Customer;

import java.sql.SQLException;

public class RegisterCustomerController {
    public Button btnClose;
    public TextField txtCustomerId;
    public TextField txtCustomerName;
    public TextField txtAddress;
    public TextField txtPhone;
    public Button btnSave;
    public Button btnCancel;
    public AnchorPane root;
    private String phone;
    private SimpleStringProperty id;
    private SimpleStringProperty name;

    public void initialize() throws SQLException {
        Platform.runLater(()->{
            try {
                String customerId = CustomerDataAccess.getLastCustomerId();
                txtCustomerId.setText(String.format("C%05d", Integer.parseInt(customerId.replace("C",""))+1));
                txtPhone.setText(phone);
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Couldn't establish a database connection.").show();
            }
        });
    }

    public void initData(String phone, SimpleStringProperty id, SimpleStringProperty name) {
        this.phone = phone;
        this.id = id;
        this.name = name;
    }
    public void btnCloseOnAction(ActionEvent actionEvent) {
        ((Stage)root.getScene().getWindow()).close();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!isDataValid()) return;
        Customer newCustomer = new Customer(txtCustomerId.getText(), txtCustomerName.getText().strip(), txtAddress.getText().strip(), phone);
        try {
            CustomerDataAccess.setCustomer(newCustomer);
            this.id.setValue(newCustomer.getCustomerId());
            this.name.setValue(newCustomer.getName());
            btnClose.fire();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't establish a connection with the database").show();
            e.printStackTrace();
        }
    }

    private boolean isDataValid() {
        if (!txtCustomerName.getText().strip().matches("[A-Za-z ]{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Customer name should have at least 3 letters long").show();
            txtCustomerName.requestFocus();
            txtCustomerName.selectAll();
            return false;
        }
        else if (!txtAddress.getText().strip().matches("[A-Za-z ]{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Customer name should have at least 3 letters long").show();
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return false;
        }
        return true;
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        ((Stage)root.getScene().getWindow()).close();
    }
}
