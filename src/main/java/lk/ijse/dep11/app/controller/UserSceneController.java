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
import lk.ijse.dep11.app.db.UserDataAccess;
import lk.ijse.dep11.app.tm.User;
import lk.ijse.dep11.app.tm.UserRole;

import java.sql.SQLException;

public class UserSceneController {
    public AnchorPane root;
    public ImageView imgProfileImage;
    public Label lblUserId;
    public Label lblUserName;
    public Button btnLogout;
    public ImageView imgViewLogo;
    public TextField txtUserId;
    public TextField txtUsername;
    public ComboBox<String> cmbGender;
    public ComboBox<UserRole> cmbUserRole;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnAdd;
    public Button btnDelete;
    public TableView<User> tblUsers;
    public TextField txtSearch;
    public Button btnNewUser;

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

        String[] itemProperties = {"id", "name", "gender", "userRole", "password"};
        for (int i = 0; i < itemProperties.length; i++) {
            if (i>0) continue;
            tblUsers.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(itemProperties[i]));
        }
        Platform.runLater(()->{
            txtSearch.requestFocus();
        });

        try {
            tblUsers.getItems().addAll(UserDataAccess.findUsers(""));
            cmbUserRole.getItems().addAll(UserDataAccess.findUserRoles(""));
            cmbGender.getItems().addAll(new String[]{"male", "female"});
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Unable to connect with the database.").show();
            e.printStackTrace();
        }

        txtSearch.textProperty().addListener((ov, prev, cur)->{
            tblUsers.getItems().clear();
            String query = "";
            if (!cur.isEmpty()) query = cur.strip();
            try {
                tblUsers.getItems().addAll(UserDataAccess.findUsers(query));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Unable to connect with the database.").show();
                e.printStackTrace();
            }
        });
        btnDelete.setDisable(true);
        btnAdd.setDisable(true);
        tblUsers.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)->{
            if (cur!=null) {
                txtUserId.setText(cur.getId());
                txtUsername.setText(cur.getName());
                try {
                    cmbUserRole.getSelectionModel().select(UserDataAccess.getUserRole(cur.getUserRoleId()));
                    cmbGender.getSelectionModel().select(cur.getGender());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                btnAdd.setText("UPDATE");
//                textFieldsSetEditable();
                txtUserId.setEditable(false);
                btnDelete.setDisable(false);
                btnAdd.setDisable(false);
            } else {
//                cleanTextFields();
            }
        });
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
    }

    public void imgViewLogoOnMouseClicked(MouseEvent mouseEvent) {
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    public void btnNewUserOnAction(ActionEvent actionEvent) {
    }


}
