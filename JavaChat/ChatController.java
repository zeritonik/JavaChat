package JavaChat;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.animation.AnimationTimer;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


public class ChatController {
    class MessageCell extends ListCell<String> {
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty)
                return;
            setText(item);
            setTextFill(getColor(item));
            setStyle("-fx-background-color: #E1F2FB");
            setFont(new Font(getFont().getName(), 16));
        }

        private Color getColor(String text) {
            String name = text.split("\s")[1];

            double[] rgb = new double[3];
            int c = 0;
            for (char ch : name.toCharArray()) {
                if (Character.isAlphabetic(ch)) {
                    c++;
                    rgb[ch % 3]++;
                }
            }

            rgb[0] = rgb[0] / c;
            rgb[0] = rgb[0] + (51.0 / 255 - rgb[0]) * 0.15;

            rgb[1] = rgb[1] / c;
            rgb[1] = rgb[1] + (81.0 / 255 - rgb[1]) * 0.15;

            rgb[2] = rgb[2] / c;
            rgb[2] = rgb[2] + (73.0 / 255 - rgb[1]) * 0.15;


            return Color.color(rgb[0], rgb[1], rgb[2]);
        }
    }

    @FXML private TextField messageField;

    @FXML private ListView<String> chatField;

    private User user;
    private AnimationTimer refreshMessagesTimer;

    public void init(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        chatField.setCellFactory(new Callback<ListView<String>,ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> lv) {
                return new MessageCell();
            }
        });
        

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
}