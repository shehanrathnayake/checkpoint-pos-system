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
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.OrderItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            if (Integer.parseInt(cur) <= Integer.parseInt(txtStock.getText().strip())) btnAdd.setDisable(false);
            else btnAdd.setDisable(true);
        });

    }
    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to logout? ");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        alert.getButtonTypes().setAll(yes, no);
        alert.showAndWait().ifPresent(result -> {
            if (result == yes) {
                try {
                    navigateToLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void navigateToLogin() throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginScene.fxml"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        ((Stage)root.getScene().getWindow()).close();
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        String itemCode = selectedItem.getItemCode();
        String description = selectedItem.getDescription();
        BigDecimal unitPrice = selectedItem.getUnitPrice();
        int qty = Integer.parseInt(txtQty.getText().strip());
        Button btnDelete = new Button("Delete");
        OrderItem newOrderItem = new OrderItem(itemCode, description, qty, unitPrice, BigDecimal.ZERO, btnDelete);
        tblOrderItems.getItems().add(newOrderItem);
        selectedItem.setQtyOnHand(selectedItem.getQtyOnHand() - qty);
        cmbItemCode.getSelectionModel().clearSelection();

        btnDelete.setOnAction(e->{
            tblOrderItems.getItems().remove(newOrderItem);
            calculateOrderTotal();
            selectedItem.setQtyOnHand(selectedItem.getQtyOnHand() + qty);
            btnPlaceOrder.setDisable(tblOrderItems.getItems().isEmpty());
            cmbItemCode.getSelectionModel().clearSelection();
        });
        tblOrderItems.refresh();
        calculateOrderTotal();
        btnPlaceOrder.setDisable(tblOrderItems.getItems().isEmpty());
    }

    private void calculateOrderTotal() {
        Optional<BigDecimal> orderTotal = tblOrderItems.getItems().stream().map(orderItem -> orderItem.getTotal()).reduce((prev, cur) -> prev.add(cur));
        lblTotal.setText("Total: Rs." + orderTotal.orElseGet(()-> BigDecimal.ZERO).setScale(2));
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
    }

    public void btnCustomerSearch(ActionEvent actionEvent) {
    }

    public void btnNewOrderOnAction(ActionEvent actionEvent) throws SQLException {
        for (TextField textField : new TextField[]{txtCustomerId, txtCustomerName, txtDiscount, txtTotalDiscount, txtQty, txtStock, txtUnitPrice}) {
            textField.clear();
        }
        cmbItemCode.getSelectionModel().clearSelection();
        lblOrderId.setText("Order ID : ".concat(String.format("OD%05d",Integer.parseInt(OrderDataAccess.getLastOrderId().substring(2)) + 1)));
        tblOrderItems.getItems().clear();
        lblTotal.setText("Total : Rs. 0.00");
        cmbItemCode.getItems().addAll(OrderDataAccess.getAllItems());
        btnAdd.setDisable(true);
        btnPlaceOrder.setDisable(true);
    }
}
