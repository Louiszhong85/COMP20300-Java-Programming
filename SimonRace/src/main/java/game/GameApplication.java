package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application
 *
 * game rule
 * Barrier: pure obstacle, not passable
 * Fire: 1 blood is deducted on stopping
 * Pit: Pause for one turn if stepped on, stop one frame before the pit if exceeded (then turn randomly Priority front, followed by left and right, really can't go after)
 * Portal: randomly teleport to a blank area
 * Hide trap, for palyer stop 1 round
 */
public class GameApplication extends Application {

    private static Stage stage = null;
    private static GameController gameController;
    public static Stage getStage() {
        return stage;
    }

    public static GameController getGameController() {
        return gameController;
    }

    @Override
    public void start(Stage stage) throws IOException {
        GameApplication.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Parent load = fxmlLoader.load();
        GameApplication.gameController = fxmlLoader.getController();
        Scene scene = new Scene(load);
        stage.setTitle("Simon Race");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {

        launch();

    }
}