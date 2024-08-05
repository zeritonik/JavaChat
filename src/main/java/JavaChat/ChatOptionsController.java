package JavaChat;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatOptionsController extends VBox {
    class MemberOptionsCell extends ListCell<String> {
        private HBox root;
        private Label memberLabel;
        private Button removeButton;

        public MemberOptionsCell() {
            try {
                root = FXMLLoader.load(getClass().getResource("ui/chat_options_cell.fxml"));
                memberLabel = (Label)root.lookup("#memberLabel");
                removeButton = (Button)root.lookup("#removeButton");
            } catch ( IOException ex ) {
                System.err.println();
                return;
            } 

            removeButton.setOnAction(e -> removeButtonPressed(e));

            setGraphic(root);
        }

        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                root.setVisible(false);
                return;
            }

            removeButton.setVisible(user.getName().compareTo(item) != 0);
            memberLabel.setText(item);

            root.setVisible(true);
        }

        private void removeButtonPressed(ActionEvent e) {
            removeMember(getItem());
        }
    }


    private User user;

    @FXML private Button addButton;
    @FXML private ListView<String> membersList;
    @FXML private TextField newMemberField;


    public ChatOptionsController(User user) {
        this.user = user;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/chat_options.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch ( IOException ex) {
            System.err.println("Can't load ChatOptionsController: " + ex);
        }
    }

    @FXML
    void initialize() {
        membersList.setCellFactory((ListView<String> lv) -> {
            return new MemberOptionsCell();
        });
        runGetMembersTask();
    }

    private void showMembers(List<String> members) {
        membersList.getItems().setAll(members);
    }       

    @FXML 
    private void addMember(ActionEvent event) {
        runAddMemberTask(newMemberField.getText());
        newMemberField.clear();
    }

    private void removeMember(String member) {
        runRemoveMemberTask(member);
        newMemberField.clear();
    }


    // Tasks creation

    private void runGetMembersTask() {
        Thread thread = new Thread(new Task<Void>() {
            protected Void call() {
                List<String>  members = user.getChatMembers();
                Platform.runLater(() -> showMembers(members));

                return null;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void runAddMemberTask(String member) {
        Task<Boolean> task = new Task<Boolean>() {
            protected Boolean call() {
                return user.addChatMember(member);
            }
        };
        task.setOnSucceeded(e -> {
            runGetMembersTask();
            if (!task.getValue()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Добавление участника");
                alert.setHeaderText(String.format("Ошибка при добавлении участника '%s'!", member));
                alert.setContentText("Проверьте правильность написания имени.");
                alert.showAndWait();
            }
        });
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void runRemoveMemberTask(String member) {
        Task<Boolean> task = new Task<Boolean>() {
            protected Boolean call() {
                return user.removeChatMember(member);
            }
        };
        task.setOnSucceeded(e -> runGetMembersTask());
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}

