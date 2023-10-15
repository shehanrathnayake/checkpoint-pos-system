package lk.ijse.dep11.app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.dep11.app.db.UserDataAccess;
import lk.ijse.dep11.app.tm.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.sql.SQLException;

public class LoginSceneController {
    public AnchorPane root;
    public TextField txtUserId;
    public TextField txtPassword;
    public Button btnLogin;
    public Button btnCancel;

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        try {
            User matchedUser = UserDataAccess.getUser(txtUserId.getText().strip());
            if (matchedUser == null) {
                new Alert(Alert.AlertType.ERROR, "No such user is registered.").show();
                txtUserId.requestFocus();
                txtUserId.selectAll();
            }
//            else if (!matchedUser.getPassword().equals(DigestUtils.sha256Hex(txtPassword.getText().strip()))) {
//                new Alert(Alert.AlertType.ERROR, "UserID and password mismatched. Enter correct password").show();
//                txtPassword.requestFocus();
//                txtPassword.selectAll();
//            }
            else {
                String scenePath ="";
                if (matchedUser.getUserRoleId() == 2) scenePath = "/view/PlaceOrderScene.fxml";
                else scenePath = "/view/AdminDashboardScene.fxml";
                UserDetails.setLoggedUser(matchedUser);
                navigateToScene(scenePath);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to establish a connection with the database, try again later").show();
            e.printStackTrace();
            txtUserId.requestFocus();
            txtUserId.selectAll();
        }
    }

    private void navigateToScene(String scenePath) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(scenePath))));
        stage.setTitle("CheckPoint POS System");
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();
        ((Stage) root.getScene().getWindow()).close();
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}
