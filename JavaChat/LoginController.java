package JavaChat;

import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.*;
import javafx.concurrent.Worker.State;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Arc;



public class LoginController {
    @FXML private TextField loginField;
    @FXML private TextField passwordField;
    @FXML private Text errorText;

    @FXML Arc waiter;

    Task<Void> loginTask;

    LoginLoadingAnimation animation;

    // called when all FXML fields are initialized
    @FXML
    public void initialize() {
        loginTask = createLoginTask();
        animation = new LoginLoadingAnimation(waiter);
    }


    @FXML 
    public void connectButtonPressed(ActionEvent e){
        if (loginTask.getState() != State.READY) 
            return;

        animation.start();

        new Thread(loginTask).start();
    }

    private boolean tryLoginUser() {
        User user = new User(loginField.getText(), passwordField.getText());

        if (!user.getState()) {
            errorText.setVisible(true);
            animation.stop();
            return false;
        }

        Platform.runLater(() -> {
            try { 
                loadChat(user); 
            } catch ( IOException ex ) { 
                loginTask = createLoginTask();
            };
        });

        return true;
    }

    private void loadChat(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage)loginField.getScene().getWindow();

        ChatController ctrl = loader.getController();
        ctrl.init(user);

        stage.setScene(scene);
        animation.stop();
    }


    @FXML public void startedTyping(KeyEvent e) {
        errorText.setVisible(false);
    }


    Task<Void> createLoginTask() {
        return new Task<Void>() {
            @Override protected Void call() {
                if (!tryLoginUser());
                    loginTask = createLoginTask();
                return null;
            };
        };
    }
}
