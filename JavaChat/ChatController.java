package JavaChat;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


public class ChatController {
    @FXML private TextField messageField;

    @FXML private ListView<String> chatField;

    @FXML public void sendButtonPressed(ActionEvent e) {
        user.sendMessage(messageField.getText());
        messageField.clear();
    }

    @FXML public void enterSendPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER){
            sendButtonPressed(null);
        }
    }

    public void refreshMessagesLoop() {
        AnimationTimer at = new AnimationTimer() {
            public long last = 0;
            @Override
            public void handle(long now) {
                if ((float)(now - last) > 3e8) {
                    last = now;
                    showMessages(user.getMessages());
                }
            }
        };

        at.start();
    }

    public void showMessages(List<ChatMessage> messages) {
        final SimpleDateFormat dformat = new SimpleDateFormat("HH:mm:ss");
        ObservableList<String> ls = FXCollections.observableList(
            messages.stream().map( 
                (ChatMessage mes) -> { 
                    return String.format("[%s] %s - %s",  dformat.format(mes.date_time),  mes.sender, mes.content); 
            }).collect(Collectors.toList())
        );
        chatField.setItems(ls);
    }


    private User user;

    public void init(User user) {
        this.user = user;
        this.refreshMessagesLoop();
    }
}
