package lk.ijse.dep11.app.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class WindowNavigation {
    public static void loggingOut(AnchorPane root) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to logout? ");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        alert.getButtonTypes().setAll(yes, no);
        alert.showAndWait().ifPresent(result -> {
            if (result == yes) {
                try {
                    UserDetails.setLoggedUser(null);
                    UserDetails.setPermittedUser(null);
                    navigateToLogin();
                    ((Stage)root.getScene().getWindow()).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void navigateToLogin() throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(WindowNavigation.class.getResource("/view/LoginScene.fxml"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();

    }

    public static void navigateToDashboard(AnchorPane root) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(WindowNavigation.class.getResource("/view/AdminDashboardScene.fxml"))));
        stage.show();
    }
}