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
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Order;

import java.io.IOException;
import java.sql.SQLException;

public class OrderSceneController {

    public AnchorPane root;
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ImageView imgViewLogo;
    public TableView<Order> tblOrders;
    public TextField txtSearch;
    public Button btnView;

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

        String[] itemProperties = {"orderId", "orderDate", "total", "userId", "userName", "customerId", "customerName", };
        for (int i = 0; i < itemProperties.length; i++) {
            tblOrders.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(itemProperties[i]));
        }

        try {
            tblOrders.getItems().addAll(OrderDataAccess.findOrders(""));

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Couldn't establish a database connection").show();
            e.printStackTrace();
        }

        txtSearch.textProperty().addListener((ov,pre,cur)->{
            tblOrders.getItems().clear();
            try {
                tblOrders.getItems().addAll(OrderDataAccess.findOrders(txtSearch.getText().strip()));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,"Couldn't establish a database connection").show();
                e.printStackTrace();
            }
        });

        tblOrders.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)->{
            btnView.setDisable(cur==null);
        });

        Platform.runLater(()->{
            txtSearch.requestFocus();
            btnView.setDisable(true);
        });
    }
    public void btnLogoutOnAction(ActionEvent actionEvent) {
        WindowNavigation.loggingOut(root);
    }

    public void imgViewLogoOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        WindowNavigation.navigateToDashboard(root);
    }

    public void btnViewOnAction(ActionEvent actionEvent) {
    }
}
