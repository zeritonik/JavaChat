package JavaChat;

import java.io.IOException;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    @FXML private Arc waiter;

    @FXML public void connectButtonPressed(ActionEvent e){
        User user = new User(loginField.getText(), passwordField.getText());
        if (!user.getState()) {
            errorText.setVisible(true);
            return;
        }

        try { loadChat(user); } catch ( IOException ex ) {};
    }

    private void loadChat(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage)loginField.getScene().getWindow();

        ChatController ctrl = loader.getController();
        ctrl.init(user);

        stage.setScene(scene);
    }

    @FXML public void startedTyping(KeyEvent e) {
        errorText.setVisible(false);
    }
}
