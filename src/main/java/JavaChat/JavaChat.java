package JavaChat;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;

public class JavaChat extends Application {

    public static void main(String[] args) {
        ChatDB.loadDriver();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new LoginController();

        Scene scene = new Scene(root);

        stage.setTitle("JavaChat");
        stage.setScene(scene);

        stage.show();
    }
}