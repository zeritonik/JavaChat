package JavaChat;

import java.io.IOException;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Arc;



public class LoginController {
    @FXML private TextField loginField;
    @FXML private TextField passwordField;
    @FXML private Text errorText;


    @FXML Arc waiter;
    RotateTransition rt;
    User user;

    @FXML 
    public void connectButtonPressed(ActionEvent e){     
        Task<Integer> task = new Task<Integer>() {
            LoginController con = LoginController.this;

            @Override protected Integer call() throws IOException {
                con.tryLoginUser();
                return 0;
            };
        };

        task.run();
    }

    synchronized private void tryLoginUser() {
        startLoadingAnimation();
        user = new User(loginField.getText(), passwordField.getText());

        if (!user.getState()) {
            errorText.setVisible(true);
            stopLoadingAnimation();
            return;
        }

        Platform.runLater(() -> {
            try { loadChat(); } catch ( IOException ex ) {};
        });
    }

    synchronized private void loadChat() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage)loginField.getScene().getWindow();

        ChatController ctrl = loader.getController();
        ctrl.init(user);

        stage.setScene(scene);
        stopLoadingAnimation();
    }


    public void startLoadingAnimation() {
        waiter.setVisible(true);
        rt = new RotateTransition(Duration.millis(1000), waiter);
        rt.setFromAngle(0);
        rt.setToAngle(359);
        rt.setCycleCount(25);

        rt.play();
    }

    public void stopLoadingAnimation() {
        rt.stop();
        waiter.setVisible(false);
    }


    @FXML public void startedTyping(KeyEvent e) {
        errorText.setVisible(false);
    }
}
