package lk.ijse.dep11.app.controller;

import javafx.scene.control.Label;

public class AdminDashboardSceneController {
    public Label lblLoggedUserName;

    public void initialize() {
        lblLoggedUserName.setText(UserDetails.getLoggedUser().getFirstName());
    }
}
