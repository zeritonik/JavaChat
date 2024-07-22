package JavaChat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;


public class JavaChat extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginform.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("JavaChat");
        stage.setScene(scene);
        
        loader.getController();

        stage.show();
    }
}