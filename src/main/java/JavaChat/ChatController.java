package JavaChat;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.animation.AnimationTimer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class ChatController extends VBox{
    class MessageCell extends ListCell<ChatMessage> {
        private AnchorPane root;
        private Label messageLabel;
        private Label senderLabel;
        private Label dateLabel;

        public MessageCell() {
            try {
                root = (AnchorPane)FXMLLoader.load(getClass().getResource("ui/messagecell.fxml"));
            } catch (IOException e) {
                System.err.println("Can't load MessageCell resource: " + e);
            }
            messageLabel = (Label)root.lookup(".message-label");
            senderLabel = (Label)root.lookup(".sender-label");
            dateLabel = (Label)root.lookup(".date-label");
            setGraphic(root);

            VBox vb = ((VBox)root.lookup(".message-vbox"));
        }

        @Override protected void updateItem(ChatMessage item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty)
                return;

            final SimpleDateFormat dformat = new SimpleDateFormat("HH:mm");

            messageLabel.setText(item.content);
            senderLabel.setText(item.sender);
            dateLabel.setText(dformat.format(item.date_time));
        }
    }

    @FXML private TextField messageField;

    @FXML private ListView<ChatMessage> chatField;

    private User user;
    private AnimationTimer refreshMessagesTimer;

    public ChatController(User user) throws IOException {
        super();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/chat.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        this.user = user;
    }

    @FXML
    public void initialize() {
        // chatCellFactory
        chatField.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override public ListCell<ChatMessage> call(ListView<ChatMessage> lv) {
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

    public void showMessages(List<ChatMessage> messages) {
        chatField.getItems().setAll(user.getMessages());
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
}