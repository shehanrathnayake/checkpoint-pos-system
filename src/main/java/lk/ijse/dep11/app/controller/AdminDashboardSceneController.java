package lk.ijse.dep11.app.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class AdminDashboardSceneController {
    public Label lblLoggedUserName;
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ImageView imgViewLogo;

    public void initialize() {
        lblLoggedUserName.setText(UserDetails.getLoggedUser().getFirstName());
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
    }
}
