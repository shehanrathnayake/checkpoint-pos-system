package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep11.app.common.UserDetails;
import lk.ijse.dep11.app.common.WindowNavigation;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.ItemDataAccess;
import lk.ijse.dep11.app.tm.Customer;
import lk.ijse.dep11.app.tm.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class CustomerSceneController {
    public AnchorPane root;
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ImageView imgViewLogo;
    public TextField txtCustomerId;
    public TextField txtCustomerName;
    public TextField txtTelephone;
    public TextField txtAddress;
    public Button btnAdd;
    public Button btnDelete;
    public TableView<Customer> tblCustomers;
    public TextField txtSearch;
    public Button btnNewCustomer;

    public void initialize() {
        final String PROFILE_IMAGE_NAME;
        if (UserDetails.getLoggedUser().getGender().equals("male")) PROFILE_IMAGE_NAME = "male-avatar.jpg";
        else PROFILE_IMAGE_NAME = "female-avatar.jpg";
        Platform.runLater(()->{
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/logout.png")));
            btnLogout.setGraphic(imageView);
            imgProfileImage.setImage(new Image(getClass().getResourceAsStream("/img/".concat(PROFILE_IMAGE_NAME))));
            lblUserId.setText(UserDetails.getLoggedUser().getId());
            lblUserName.setText(UserDetails.getLoggedUser().getFirstName());
        });

        String[] itemProperties = {"customerId", "name", "phone", "address"};
        for (int i = 0; i < itemProperties.length; i++) {
            tblCustomers.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(itemProperties[i]));
        }
        Platform.runLater(()->{
            txtSearch.requestFocus();
        });

        try {
            tblCustomers.getItems().addAll(CustomerDataAccess.findCustomers(""));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Unable to connect with the database.").show();
            e.printStackTrace();
        }

        txtSearch.textProperty().addListener((ov, prev, cur)->{
            tblCustomers.getItems().clear();
            String query = "";
            if (!cur.isEmpty()) query = cur.strip();
            try {
                tblCustomers.getItems().addAll(CustomerDataAccess.findCustomers(query));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Unable to connect with the database.").show();
                e.printStackTrace();
            }
        });
        btnDelete.setDisable(true);
        btnAdd.setDisable(true);
        tblCustomers.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)->{
            if (cur!=null) {
                txtCustomerId.setText(cur.getCustomerId());
                txtCustomerName.setText(cur.getName());
                txtTelephone.setText(cur.getPhone());
                txtAddress.setText(cur.getAddress());

                btnAdd.setText("UPDATE");
                textFieldsSetEditable();
                txtCustomerId.setEditable(false);
                btnDelete.setDisable(false);
                btnAdd.setDisable(false);
            } else {
                cleanTextFields();
            }
        });
    }

    private void cleanTextFields() {
        for (TextField textField : new TextField[]{txtSearch, txtCustomerId, txtCustomerName, txtTelephone, txtAddress}) {
            textField.clear();
        }
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        WindowNavigation.loggingOut(root);
    }

    public void imgViewLogoOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        WindowNavigation.navigateToDashboard(root);
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        try {
            if (isDataValid()) {
                Customer newCustomer = new Customer(txtCustomerId.getText().strip(), txtCustomerName.getText().strip(), txtAddress.getText().strip(), txtTelephone.getText().strip());

                if(btnAdd.getText().strip().equals("ADD")) {
                    CustomerDataAccess.setCustomer(newCustomer);
                } else {
                    CustomerDataAccess.updateCustomer(newCustomer);
                }
                btnNewCustomer.fire();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Unable to establish a database connection").show();
            e.printStackTrace();
        }
    }

    private boolean isDataValid() throws SQLException {
        if (txtCustomerName.getText().strip().length() <= 3) {
            new Alert(Alert.AlertType.ERROR, "Customer name should be at least 3 characters long.").show();
            txtCustomerName.requestFocus();
            txtCustomerName.selectAll();
            return false;
        }
        else if (!(txtTelephone.getText().strip().matches("[+][0-9]{11}")) || txtTelephone.getText().strip().matches("0[0-9]{9}")) {
            new Alert(Alert.AlertType.ERROR, "Number format should be +94711212345 or 0711212345").show();
            txtTelephone.requestFocus();
            txtTelephone.selectAll();
            return false;
        }
        else if (!txtAddress.getText().strip().matches("[A-Za-z ]{3,}")) {
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return false;
        }
        return true;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer!=null) {
            try {
                CustomerDataAccess.deleteCustomer(selectedCustomer.getCustomerId());
                btnNewCustomer.fire();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Unable to establish a database connection").show();
                e.printStackTrace();
            }
        }
    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) throws SQLException {
        cleanTextFields();
        btnAdd.setText("ADD");
        btnDelete.setDisable(true);
        setNewCustomerId();
        textFieldsSetEditable();
        txtCustomerName.requestFocus();
        tblCustomers.getItems().clear();
        tblCustomers.getItems().addAll(CustomerDataAccess.findCustomers(""));
        btnAdd.setDisable(false);
    }

    private void setNewCustomerId() throws SQLException {
        String lastCustomerId = CustomerDataAccess.getLastCustomerId();
        String newCustomerId = String.format("C%05d", Integer.parseInt(lastCustomerId.replace("C",""))+1);
        Platform.runLater(()->{
            txtCustomerId.setText(newCustomerId);
        });
    }

    private void textFieldsSetEditable() {
        for (TextField textField : new TextField[]{txtCustomerName, txtTelephone, txtAddress}) {
            textField.setEditable(true);
        }
    }
}
