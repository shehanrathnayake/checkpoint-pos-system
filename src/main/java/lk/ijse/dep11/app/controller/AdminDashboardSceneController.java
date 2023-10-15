package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.app.common.UserDetails;
import lk.ijse.dep11.app.common.WindowNavigation;

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
    public Button btnManageOrders;

    public void initialize() {
        final String PROFILE_IMAGE_NAME;
        if (UserDetails.getLoggedUser().getGender().equals("male")) PROFILE_IMAGE_NAME = "male-avatar.jpg";
        else PROFILE_IMAGE_NAME = "female-avatar.jpg";
        Button[] buttons = {btnLogout, btnManageInventory, btnManageUsers, btnManageCustomers, btnManageOrders};
        String[] imageNames = {"logout.png","inventory.png", "users.png", "customer.png", "orders.png"};

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

    public void btnManageInventoryOnAction(ActionEvent actionEvent) throws IOException {
        String sceneName = "InventoryScene.fxml";
        String sceneTitle = "Inventory Management";
        navigateToFeatureScene(sceneName, sceneTitle);
    }

    public void btnManageUsersOnAction(ActionEvent actionEvent) throws IOException {
        String sceneName = "UserScene.fxml";
        String sceneTitle = "Users Management";
        navigateToFeatureScene(sceneName, sceneTitle);
    }

    public void btnManageCustomersOnAction(ActionEvent actionEvent) throws IOException {
        String sceneName = "CustomerScene.fxml";
        String sceneTitle = "Customers Management";
        navigateToFeatureScene(sceneName, sceneTitle);
    }

    public void btnManageOrdersOnAction(ActionEvent actionEvent) throws IOException {
        String sceneName = "OrderScene.fxml";
        String sceneTitle = "Orders Management";
        navigateToFeatureScene(sceneName, sceneTitle);
    }

    private void navigateToFeatureScene(String sceneName, String sceneTitle) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/".concat(sceneName)))));
        stage.setTitle(sceneTitle);
        stage.setMaximized(true);
        stage.show();
        ((Stage)root.getScene().getWindow()).close();
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        WindowNavigation.loggingOut();
        ((Stage)root.getScene().getWindow()).close();
    }


}
