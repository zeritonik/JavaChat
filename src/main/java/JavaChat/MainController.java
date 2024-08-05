package JavaChat;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.scene.control.*;

public class MainController extends SplitPane{

    protected User user;

    private MenuController menuPart;
    private ChatController chatPart = null;

    public MainController(User user) throws IOException{
        super();

        this.user = user;
        setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        try {
            String css = getClass().getResource("ui/main.css").toExternalForm();
            this.getStylesheets().add(css);

            menuPart = new MenuController(user, this);
            menuPart.setPadding(new Insets(0, 3, 0, 0));

            this.getItems().add(menuPart);
        } catch ( IOException ex ) {
            System.err.println("Can't load MainController: " + ex);
            throw ex;
        }
    }

    private void createChatPart() {
        try {
            chatPart = new ChatController(user);
        } catch ( IOException ex ) {

        }
    }

    protected void openChatPart(ChatChat chat) {
        user.changeCurrentChat(chat);
        
        if (chatPart != null) {
            chatPart.closeChat();
            this.getItems().remove(chatPart);
        }

        createChatPart();
        this.getItems().add(chatPart);

        getScene().getWindow().sizeToScene();
    }

    protected void closeChatPart() {
        if (chatPart != null) {
            user.changeCurrentChat(null);
            chatPart.closeChat();
            this.getItems().remove(chatPart);
            
            getScene().getWindow().sizeToScene();
        }
    }
}
