package JavaChat;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.application.Platform;

import javafx.concurrent.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class ChatController extends SplitPane{
    class MessageCell extends ListCell<ChatMessage> {
        private AnchorPane root;
        private Label messageLabel;
        private Label senderLabel;
        private Label dateLabel;

        public MessageCell() {
            try {
                root = FXMLLoader.load(getClass().getResource("ui/chat_messagecell.fxml"));
            } catch (IOException e) {
                System.err.println("Can't load MessageCell resource: " + e);
                return;
            }
            messageLabel = (Label)root.lookup(".message-label");
            senderLabel = (Label)root.lookup(".sender-label");
            dateLabel = (Label)root.lookup(".date-label");
            setGraphic(root);
        }

        @Override protected void updateItem(ChatMessage item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty) {
                root.setVisible(false);
                return;
            }

            final SimpleDateFormat dformat = new SimpleDateFormat("HH:mm");

            messageLabel.setText(item.content);
            senderLabel.setText(item.sender);
            dateLabel.setText(dformat.format(item.date_time));
            root.setVisible(true);
        }
    }

    @FXML private Label chatLabel;

    @FXML private TextField messageField;
    @FXML private ListView<ChatMessage> chatField;
    @FXML private ToggleButton optionToggle;

    private User user;
    ChatOptionsController options = null;

    private Task<Void> refreshMessages;


    public ChatController(User user) throws IOException {
        super();

        this.user = user;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/chat.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch ( IOException ex ) {
            System.err.println("Can't load ChatController resource");
            throw ex;
        }
    }

    @FXML
    public void initialize() {
        if (user.getCurrentChat().id == ChatChat.globalChatId)
            optionToggle.setVisible(false);

        // chatLabel
        chatLabel.setText(user.getCurrentChat().chat_name);
        
        // chatCellFactory
        chatField.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override public ListCell<ChatMessage> call(ListView<ChatMessage> lv) {
                return new MessageCell();
            }
        });
        
        // messagesUpdateCycle
        refreshMessages = createRefreshTask();

        Thread refreshThread = new Thread(refreshMessages);
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void showMessages(List<ChatMessage> messages) {
        chatField.getItems().setAll(messages);
    }

    @FXML private void sendButtonPressed(ActionEvent e) {
        Thread sendThread = new Thread(createSendTask(messageField.getText()));
        sendThread.setDaemon(true);
        sendThread.start();
    }

    @FXML private void enterSendPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER){
            sendButtonPressed(null);
        }
    }

    private void toLastMessages() {
        chatField.scrollTo(chatField.getItems().size() - 1);
        messageField.clear();
    }

    @FXML private void optionToggleChanged(ActionEvent e) {
        if (options == null) {
            options = new ChatOptionsController(user);
            this.getItems().add(options);
        } else {
            this.getItems().remove(options);
            options = null;
        }
    }

    protected void closeChat() {
        refreshMessages.cancel();
    }


    // Tasks creation

    private Task<Void> createRefreshTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException{
                while (true) {
                    if (isCancelled())
                        break;

                    VirtualFlow<ListCell<String>> vf = (VirtualFlow<ListCell<String>>)chatField.lookup(".virtual-flow");

                    if (vf != null && vf.getFirstVisibleCell() != null) {
                        int lvc = vf.getLastVisibleCell().getIndex(), fvc = vf.getFirstVisibleCell().getIndex();
    
                        int dist = (lvc + fvc) / 2 + 1 - chatField.getItems().size() / 2;
                        int move = user.moveMessageStart(dist);
                        if (move != 0) {
                            chatField.scrollTo(fvc - move);
                        }
                    }
                    
                    Platform.runLater(() -> showMessages(user.getMessages()));
                    Thread.sleep(300);
                }
                return null;
            }
        };
    }

    private Task<Void> createSendTask(String text) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                user.sendMessage(text);
                user.toLastMessages();

                List<ChatMessage> messages = user.getMessages();

                Platform.runLater(() -> {
                    showMessages(messages);
                    toLastMessages();
                });

                return null;
            }
        };
    }
}