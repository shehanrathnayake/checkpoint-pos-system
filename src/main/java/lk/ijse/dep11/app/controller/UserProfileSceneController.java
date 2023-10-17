package lk.ijse.dep11.app.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.app.db.UserDataAccess;
import lk.ijse.dep11.app.tm.User;
import lk.ijse.dep11.app.tm.UserRole;

import java.sql.SQLException;

public class UserProfileSceneController {

    public AnchorPane root;
    public TextField txtFirstName;
    public TextField txtLastName;
    public Button btnChangeProfilePicture;
    public ComboBox<String> cmbGender;
    public ComboBox<UserRole> cmbUserRole;
    public PasswordField txtOldPassword;
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Button btnSave;
    public Button btnCancel;
    public Label txtUserId;
    public ToggleButton btnChangePassword;
    private User selectedUser;

    private boolean changePassword;

    public void initialize() {
        if (selectedUser != null) {
            txtUserId.setText(selectedUser.getId());
            txtFirstName.setText(selectedUser.getFirstName());
            txtLastName.setText(selectedUser.getLastName());
            cmbUserRole.getSelectionModel().select(selectedUser.getUserRoleId());
            cmbGender.getSelectionModel().select(selectedUser.getGender());
        } else {
            txtOldPassword.setDisable(true);
        }

    }
    public void initData(User selectedUser) {
        this.selectedUser=selectedUser;
    }

    public void btnChangeProfilePicture(ActionEvent actionEvent) {
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!isDataValid()) return;
        String userId = txtUserId.getText();
        String firstName = txtFirstName.getText().strip();
        String lastName = txtLastName.getText().strip();
        String gender = cmbGender.getSelectionModel().getSelectedItem();
        int userRoleId = cmbUserRole.getSelectionModel().getSelectedItem().getId();
        String password = "";
        if (selectedUser== null) password = txtNewPassword.getText();
        else password = selectedUser.getPassword();
        User newUser = new User(userId, firstName, lastName, gender, userRoleId, password);
        try {
            UserDataAccess.setUser(newUser);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Cannot save the user. Try again later").show();
            e.printStackTrace();
        }
    }

    private boolean isDataValid() {
        if (!txtFirstName.getText().strip().matches("[A-Za-z]{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Name should be more than 3 letters long").show();
            txtFirstName.requestFocus();
            txtFirstName.selectAll();
            return false;
        }
        else if (!txtLastName.getText().strip().matches("[A-Za-z]{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Name should be more than 3 letters long").show();
            txtLastName.requestFocus();
            txtLastName.selectAll();
            return false;
        }
        if (selectedUser == null) {
            if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
                new Alert(Alert.AlertType.ERROR, "New Password and Confirmed password is not matched.").show();
                txtConfirmPassword.requestFocus();
                txtConfirmPassword.selectAll();
                return false;
            }
        } else {
            if (changePassword) {
                if (!txtOldPassword.getText().equals(selectedUser.getPassword())) {
                    new Alert(Alert.AlertType.ERROR, "Old password you entered is incorrect.").show();
                    txtOldPassword.requestFocus();
                    txtOldPassword.selectAll();
                    return false;
                }
                else if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
                    new Alert(Alert.AlertType.ERROR, "New Password and Confirmed password is not matched.").show();
                    txtConfirmPassword.requestFocus();
                    txtConfirmPassword.selectAll();
                    return false;
                }
            }
        }
        return true;
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        ((Stage)root.getScene().getWindow()).close();
    }

    public void btnChangePasswordOnAction(ActionEvent actionEvent) {
        changePassword = btnChangePassword.isSelected();
    }
}
