package JavaChat;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.animation.AnimationTimer;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


public class ChatController {
    class MessageCell extends ListCell<String> {
        private Label graphic = new Label();

        public MessageCell() {
        }

        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty)
                return;

            setGraphic(graphic);
            graphic.setText(item);
        }
    }

    @FXML Menu styleMenu;

    @FXML private TextField messageField;

    @FXML private ListView<String> chatField;

    private User user;
    private AnimationTimer refreshMessagesTimer;

    public void init(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        // chatCellFactory
        chatField.setCellFactory(new Callback<ListView<String>,ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> lv) {
                return new MessageCell();
            }
        });
        
        // messagesUpdateCycle
        refreshMessagesTimer = new AnimationTimer() {
            public long last = (long)-1e9;
            @Override
            public void handle(long now) {
                if ((float)(now - last) < 3e8) 
                    return;                

                VirtualFlow<ListCell<String>> vf = (VirtualFlow<ListCell<String>>)chatField.lookup(".virtual-flow");

                if (vf != null && vf.getFirstVisibleCell() != null) {
                    int lvc = vf.getLastVisibleCell().getIndex(), fvc = vf.getFirstVisibleCell().getIndex();

                    int dist = (lvc + fvc) / 2 + 1 - chatField.getItems().size() / 2;
                    int move = user.moveMessageStart(dist);
                    if (move != 0) {
                        chatField.scrollTo(fvc - move);
                    }
                }

                last = now;
                showMessages(user.getMessages());
            }
        };
        refreshMessagesTimer.start();
    }

    @FXML public void sendButtonPressed(ActionEvent e) {
        user.sendMessage(messageField.getText());
        user.toLastMessages();
        showMessages(user.getMessages());
        
        chatField.scrollTo(chatField.getItems().size() - 1);
        messageField.clear();
    }

    @FXML public void enterSendPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER){
            sendButtonPressed(null);
        }
    }

    public void showMessages(List<ChatMessage> messages) {
        final SimpleDateFormat dformat = new SimpleDateFormat("HH:mm:ss");
        chatField.getItems().setAll(
            messages.stream().map( 
            (ChatMessage mes) -> { 
                return String.format("[%s] %s - %s",  dformat.format(mes.date_time),  mes.sender, mes.content); 
            }).collect(Collectors.toList()) 
        );
    }

    @FXML public void chooseStyle(ActionEvent e) {
    }
}