package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AdminDashboardSceneController {
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ImageView imgViewLogo;
    public Button btnManageInventory;
    public AnchorPane root;
    public Button btnManageUsers;
    public Button btnManageCustomers;
    public Button btnSeeReports;
    public Button btnManageOrders;

    public void initialize() {
        final String PROFILE_IMAGE_NAME;
        if (UserDetails.getLoggedUser().getGender().equals("male")) PROFILE_IMAGE_NAME = "male-avatar.jpg";
        else PROFILE_IMAGE_NAME = "female-avatar.jpg";
        Button[] buttons = {btnLogout, btnManageInventory, btnManageUsers, btnManageCustomers, btnSeeReports};
        String[] imageNames = {"logout.png","inventory.png", "users.png", "customer.png", "report.png"};

        Platform.runLater(()->{
            for (int i = 0; i < imageNames.length; i++) {
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/".concat(imageNames[i]))));
                if (i > 0) {
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                }
                buttons[i].setGraphic(imageView);
            }
            imgProfileImage.setImage(new Image(getClass().getResourceAsStream("/img/".concat(PROFILE_IMAGE_NAME))));
            lblUserId.setText(UserDetails.getLoggedUser().getId());
            lblUserName.setText(UserDetails.getLoggedUser().getFirstName());
        });
    }

    public void btnManageInventoryOnAction(ActionEvent actionEvent) {

    }

    public void btnManageUsersOnAction(ActionEvent actionEvent) {
    }

    public void btnManageCustomersOnAction(ActionEvent actionEvent) {
    }

    public void btnManageOrdersOnAction(ActionEvent actionEvent) {
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
}
