package lk.ijse.dep11.app.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashSceneController {
    public AnchorPane root;

    public void initialize() {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(1500));
        pauseTransition.setOnFinished(e -> {
            try {
                Stage stage = (Stage) root.getScene().getWindow();
                Scene loginScene = new Scene(FXMLLoader.load(getClass().getResource("/view/LoginScene.fxml")));
                stage.setScene(loginScene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        pauseTransition.play();
    }
}
