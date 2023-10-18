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

    private String sceneName;
    private String sceneTitle;
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
        sceneName = "InventoryScene.fxml";
        sceneTitle = "Inventory Management";
        WindowNavigation.navigateToWindow(sceneName, sceneTitle, root);
    }

    public void btnManageUsersOnAction(ActionEvent actionEvent) throws IOException {
        sceneName = "UserScene.fxml";
        sceneTitle = "Users Management";
        WindowNavigation.navigateToWindow(sceneName, sceneTitle, root);
    }

    public void btnManageCustomersOnAction(ActionEvent actionEvent) throws IOException {
        sceneName = "CustomerScene.fxml";
        sceneTitle = "Customers Management";
        WindowNavigation.navigateToWindow(sceneName, sceneTitle, root);
    }

    public void btnManageOrdersOnAction(ActionEvent actionEvent) throws IOException {
        sceneName = "OrderScene.fxml";
        sceneTitle = "Orders Management";
        WindowNavigation.navigateToWindow(sceneName, sceneTitle, root);
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        WindowNavigation.loggingOut(root);
    }


}
