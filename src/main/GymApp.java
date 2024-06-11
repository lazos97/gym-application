package main;

import java.io.IOException;
import main.helpers.FXMLHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GymApp extends Application {

    //ΑΝΟΙΓΕΙ ΤΟ Main.fxml ΤΟ ΟΠΟΙΟ ΕΙΝΑΙ ΤΟ ΑΡΧΙΚΟ SCENE LOGIN-SIGN UP FORM
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("resources/views/Main.fxml"));
            Scene scene = new Scene(root);
            FXMLHelper controller = new FXMLHelper(stage);
            controller.setupMouseEvents(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
               
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
