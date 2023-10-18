package lk.ijse.dep11.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.app.db.UserDataAccess;
import lk.ijse.dep11.app.tm.UserRole;

import java.sql.SQLException;

public class UserRoleSceneController {
    public AnchorPane root;
    public TableView<UserRole> tblUserRole;
    public TextField txtUserRole;
    public Button btnSave;
    public Button btnDelete;
    public Button btnClose;

    public void initialize() {
        tblUserRole.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("role"));
        try {
            tblUserRole.getItems().addAll(UserDataAccess.findUserRoles(""));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't establish a database connection").show();
            e.printStackTrace();
            return;
        }
        tblUserRole.getSelectionModel().selectedItemProperty().addListener((ov, pre, cur)->{
            if (cur!=null) {
                txtUserRole.setText(cur.getRole());
                btnSave.setText("UPDATE");
            }
            else {
                txtUserRole.clear();
                btnSave.setText("SAVE");
            }
            btnDelete.setDisable(cur==null);
        });
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        txtUserRole.textProperty().addListener((ov,pre,cur)->{
            btnSave.setDisable(cur==null);
        });
    }
    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            if (!isDataValid()) return;
            if (btnSave.getText().equals("SAVE")) {
                UserDataAccess.setUserRole(txtUserRole.getText().strip());
            } else {
                UserRole selectedUserRole = tblUserRole.getSelectionModel().getSelectedItem();
                UserDataAccess.updateUserRole(new UserRole(selectedUserRole.getId(),txtUserRole.getText().strip()));
            }
            txtUserRole.clear();
            tblUserRole.getItems().clear();
            tblUserRole.getItems().addAll(UserDataAccess.findUserRoles(""));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't establish a database connection.").show();
            e.printStackTrace();
        }
    }

    private boolean isDataValid() throws SQLException {
        if (!txtUserRole.getText().strip().matches("[A-Za-z ]{2,}")) {
            new Alert(Alert.AlertType.ERROR, "User role should be at least 2 letters long").show();
            txtUserRole.requestFocus();
            txtUserRole.selectAll();
            return false;
        } else if (UserDataAccess.findUserRoles(txtUserRole.getText().strip()).size() > 0) {
            new Alert(Alert.AlertType.ERROR, "User role already exists").show();
            txtUserRole.requestFocus();
            txtUserRole.selectAll();
            return false;
        }
        return true;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        UserRole selectedUserRole = tblUserRole.getSelectionModel().getSelectedItem();
        try{
            if(!UserDataAccess.isUserRoleUsed(selectedUserRole)) {
                UserDataAccess.deleteUserRole(selectedUserRole);
                tblUserRole.getItems().clear();
                tblUserRole.getItems().addAll(UserDataAccess.findUserRoles(""));
            } else {
                new Alert(Alert.AlertType.ERROR, "Cannot delete. Selected user role is already in use.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't establish a database connection").show();
            e.printStackTrace();
        }
    }

    public void btnCloseOnAction(ActionEvent actionEvent) {
        ((Stage)root.getScene().getWindow()).close();
    }
}
