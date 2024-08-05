package JavaChat;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

public class MenuController extends VBox {
    class ChatCell extends ListCell<ChatChat> {
        private static ToggleGroup tg = new ToggleGroup();

        private HBox root;
        private ToggleButton chatToggle;
        private Button leaveButton;

        public ChatCell() {
            try {
                root = FXMLLoader.load(getClass().getResource("ui/menu_chatcell.fxml"));
            } catch ( IOException ex ) {
                System.err.println("Can't load ChatCell resource: " + ex);
                return;
            }

            chatToggle = (ToggleButton)root.lookup("#chatToggle");
            leaveButton = (Button)root.lookup("#leaveButton");

            chatToggle.setToggleGroup(tg);
            chatToggle.setOnAction(e -> onChatChange(e));

            leaveButton.setOnAction(e -> onChatLeave(e));

            setGraphic(root);
        }

        @Override protected void updateItem(ChatChat item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                root.setVisible(false);
                return;
            }

            leaveButton.setVisible(item.id != ChatChat.globalChatId);

            if (user.getCurrentChat() != null && user.getCurrentChat().compareTo(item) == 0) {
                chatToggle.setSelected(true);
            }

            chatToggle.setText(item.chat_name);
            root.setVisible(true);
        }

        private void onChatChange(ActionEvent e) {
            if (tg.getSelectedToggle() == null) {
                mainController.closeChatPart();
                return;
            }
            
            mainController.openChatPart(getItem());
        }

        private void onChatLeave(ActionEvent e) {
            ChatChat chat = getItem();
            getListView().getItems().remove(chat);

            if (user.getCurrentChat() != null && user.getCurrentChat().compareTo(chat) == 0)
                mainController.closeChatPart();

            runLeaveTask(chat);
        }
    }

    @FXML
    private ListView<ChatChat> chatsList;

    @FXML
    private TextField newChatEdit;

    private User user;
    private MainController mainController;

    public MenuController(User user, MainController mainController) throws IOException {
        super();

        this.user = user;
        this.mainController = mainController;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/menu.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch ( IOException ex ) {
            System.err.println("Can't load MenuController: " + ex);
            throw ex;
        }
    }

    @FXML
    private void initialize() {
        chatsList.setCellFactory(new Callback<ListView<ChatChat>, ListCell<ChatChat>>() {
            @Override public ListCell<ChatChat> call(ListView<ChatChat> lv) {
                return new ChatCell();
            }
        });

        runRefreshTask();
    }

    @FXML
    protected void createChatPressed(ActionEvent e) {
        runCreateChatTask(newChatEdit.getText());
        newChatEdit.clear();
    }

    @FXML
    protected void refreshPressed(ActionEvent e) {
        runRefreshTask();
    }

    private void refresh(List<ChatChat> chats) {
        chatsList.getItems().setAll(chats);
    }


    // Tasks creation

    private void runRefreshTask() {
        Thread th = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                List<ChatChat> chats = user.getChats();
                Platform.runLater(() -> refresh(chats));
                return null;
            }
        });
        th.setDaemon(true);
        th.start();
    }

    private void runCreateChatTask(String chat_name) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return user.createChat(chat_name);
            }
        };
        task.setOnSucceeded((WorkerStateEvent e) -> runRefreshTask());

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void runLeaveTask(ChatChat chat) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return user.leaveChat(chat);
            }
        };
        task.setOnSucceeded((WorkerStateEvent e) -> runRefreshTask());

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}
