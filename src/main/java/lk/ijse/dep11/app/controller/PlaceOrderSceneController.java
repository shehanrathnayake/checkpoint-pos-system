package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.dep11.app.common.UserDetails;
import lk.ijse.dep11.app.common.WindowNavigation;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Customer;
import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.OrderItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlaceOrderSceneController {
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ComboBox<Item> cmbItemCode;
    public TextField txtStock;
    public TextField txtUnitPrice;
    public TextField txtDiscount;
    public ImageView imgViewLogo;
    public TextField txtQty;
    public Button btnAdd;
    public TextField txtCustomerId;
    public TextField txtCustomerName;
    public Button btnCustomerSearch;
    public TableView<OrderItem> tblOrderItems;
    public TextField txtTotalDiscount;
    public ComboBox<String> cmbDiscountDecider;
    public Label lblTotal;
    public Button btnPlaceOrder;
    public Button btnNewOrder;
    public Label lblOrderId;
    public Label lblDate;
    public AnchorPane root;
    public TextField txtDescription;
    public TextField txtCustomerPhone;

    public void initialize() {
        String[] orderItemColumns = {"orderItemCode", "description", "qty", "unitPrice", "discount", "total", "btnDelete"};
        for (int i = 0; i < 7; i++) {
            tblOrderItems.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(orderItemColumns[i]));
        }

        lblDate.setText("Date : ".concat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        Platform.runLater(()->{
            cmbItemCode.requestFocus();
            Image searchImage = new Image(getClass().getResourceAsStream("/img/search.png"));
            ImageView searchImageView = new ImageView(searchImage);
            btnCustomerSearch.setGraphic(searchImageView);

            Image logoutImage = new Image(getClass().getResourceAsStream("/img/logout.png"));
            ImageView logoutImageView = new ImageView(logoutImage);
            btnLogout.setGraphic(logoutImageView);
        });
        btnNewOrder.fire();

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)->{
            if (cur==null) {
                for (TextField textField : new TextField[]{txtDescription, txtStock, txtUnitPrice, txtQty}) {
                    textField.clear();
                    textField.setDisable(true);
                }
            } else {
                for (TextField textField : new TextField[]{txtDescription, txtStock, txtUnitPrice}) {
                    textField.setDisable(false);
                }
                txtDescription.setText(cur.getDescription());
                txtStock.setText(String.valueOf(cur.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(cur.getUnitPrice()));
                if (Integer.parseInt(txtStock.getText().strip()) > 0) txtQty.setDisable(false);
            }
        });

        txtQty.textProperty().addListener((ov, pre, cur)->{
            if (cur.strip().matches("\\d+") && Integer.parseInt(cur.strip()) <= Integer.parseInt(txtStock.getText().strip())) btnAdd.setDisable(false);
            else btnAdd.setDisable(true);
        });

    }
    public void btnLogoutOnAction(ActionEvent actionEvent) {
        WindowNavigation.loggingOut(root);
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        int qty = Integer.parseInt(txtQty.getText().strip());

        Optional<OrderItem> optOrderItem = tblOrderItems.getItems().stream().filter(item -> selectedItem.getItemCode().equals(item.getOrderItemCode())).findFirst();
        if(optOrderItem.isEmpty()) {
            String itemCode = selectedItem.getItemCode();
            String description = selectedItem.getDescription();
            BigDecimal unitPrice = selectedItem.getUnitPrice();
            Button btnDelete = new Button("Delete");
            OrderItem newOrderItem = new OrderItem(itemCode, description, qty, unitPrice, BigDecimal.ZERO, btnDelete);
            tblOrderItems.getItems().add(newOrderItem);

            btnDelete.setOnAction(e->{
                tblOrderItems.getItems().remove(newOrderItem);
                calculateOrderTotal();
                selectedItem.setQtyOnHand(selectedItem.getQtyOnHand() + qty);
                btnPlaceOrder.setDisable(tblOrderItems.getItems().isEmpty());
                cmbItemCode.getSelectionModel().clearSelection();
            });
        } else {
            OrderItem orderItem = optOrderItem.get();
            orderItem.setQty(orderItem.getQty() + qty);
            tblOrderItems.refresh();
        }

        selectedItem.setQtyOnHand(selectedItem.getQtyOnHand() - qty);
        cmbItemCode.getSelectionModel().clearSelection();

        calculateOrderTotal();
        btnPlaceOrder.setDisable(tblOrderItems.getItems().isEmpty());
    }

    private void calculateOrderTotal() {
        Optional<BigDecimal> orderTotal = tblOrderItems.getItems().stream().map(orderItem -> orderItem.getTotal()).reduce((prev, cur) -> prev.add(cur));
        lblTotal.setText("TOTAL : Rs. " + orderTotal.orElseGet(()-> BigDecimal.ZERO).setScale(2));
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        try {
            OrderDataAccess.saveOrder(tblOrderItems.getItems(), lblOrderId.getText().replace("Order ID : ", ""), UserDetails.getLoggedUser().getId());
            printBill();
            btnNewOrder.fire();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the order. Try again later.").show();
            e.printStackTrace();
        }
    }

    private void printBill() {
        JasperDesign jasperDesign;
        try {
            jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/print/pos-bill.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblOrderId.getText().replace("Order ID : ", ""));
            reportParams.put("date", lblDate.getText().replace("Date : ", ""));
            reportParams.put("teller-id", lblUserId.getText());
            reportParams.put("teller-name", lblUserName.getText());
            reportParams.put("total", lblTotal.getText().replace("TOTAL : Rs. ", ""));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams, new JRBeanCollectionDataSource(tblOrderItems.getItems()));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill").show();
            e.printStackTrace();
        }
    }

    public void btnCustomerSearch(ActionEvent actionEvent) {
        if (!isPhoneNumberValid()) return;
        try {
            String customerNumber = txtCustomerPhone.getText().strip();
            List<Customer> customer = CustomerDataAccess.findCustomers(customerNumber);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't establish a database connection").show();
            e.printStackTrace();
        } catch (NullPointerException e) {
            Alert newAlert = new Alert(Alert.AlertType.INFORMATION, "No existing customer found. Do you want to register a new customer?");
            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            newAlert.getButtonTypes().setAll(yes, no);
            newAlert.showAndWait().ifPresent(result -> {
                if (result == yes) {
                }
            });
        }
    }

    private boolean isPhoneNumberValid() {
        if (!(txtCustomerPhone.getText().strip().matches("[+]\\d{11}") || txtCustomerPhone.getText().strip().matches("0\\d{9}"))) {
            new Alert(Alert.AlertType.ERROR, "Enter a correct number format").show();
            return false;
        }
        return true;
    }

    public void btnNewOrderOnAction(ActionEvent actionEvent) throws SQLException {
        for (TextField textField : new TextField[]{txtCustomerId, txtCustomerName, txtDiscount, txtTotalDiscount, txtQty, txtStock, txtUnitPrice}) {
            textField.clear();
        }
        cmbItemCode.getSelectionModel().clearSelection();
        lblOrderId.setText("Order ID : ".concat(String.format("OD%05d",Integer.parseInt(OrderDataAccess.getLastOrderId().substring(2)) + 1)));
        tblOrderItems.getItems().clear();
        lblTotal.setText("TOTAL : Rs. 0.00");
        cmbItemCode.getItems().addAll(OrderDataAccess.getAllItems());
        btnAdd.setDisable(true);
        btnPlaceOrder.setDisable(true);
    }
}
