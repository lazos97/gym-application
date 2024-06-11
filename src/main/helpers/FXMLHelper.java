package main.helpers;

//CLASS HELPER ΓΙΑ ΝΑ ΕΛΕΓΧΟΥΜΕ ΤA FXML DASHBOARD ΚΑΙ ΝΑ ΤA ΜΕΤΑΚΙΝΟΥΜΕ ΟΠΟΥ ΘΕΛΟΥΜΕ.
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FXMLHelper {
    private double x = 0;
    private double y = 0;
    private final Stage stage;

    public FXMLHelper(Stage stage) {
        this.stage = stage;
    }

    public void setupMouseEvents(Parent root) {
        root.setOnMousePressed(this::handleMousePressed);
        root.setOnMouseDragged(this::handleMouseDragged);
        root.setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
        stage.setOpacity(0.8);
    }

    private void handleMouseReleased(MouseEvent event) {
        stage.setOpacity(1.0);
    }
}