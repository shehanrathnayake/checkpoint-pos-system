package lk.ijse.dep11.app.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginSceneController {
    public AnchorPane root;
    public TextField txtUserId;
    public TextField txtPassword;
    public Button btnLogin;
    public Button btnCancel;

    public void btnLoginOnAction(ActionEvent actionEvent) {

    }
    private boolean isDataValid() {
        if (true) {}
            return true;
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}
