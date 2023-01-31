package game;

import javafx.scene.Node;

/**
 * Utils
 * The main function is to implement game background maps to achieve.
 * For example:
 * the Size
 * color
 * position
 *
 */

public class Utils {
    public static void setBackgroundImageStyle(Node node, String url, String size) {
        try {
            node.setStyle("-fx-background-image: url(" + url + ") no-repeat;" +
                    "-fx-background-size: " + size + ";" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;");
        } catch (Exception e) {
            node.setStyle("-fx-background-image: " + url + "; no-repeat;" +
                    "-fx-background-size: " + size + ";" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;");
        }
    }

    public static void setBackgroundImageStyle(Node node, String url) {
        setBackgroundImageStyle(node, url, "100% 100%");
    }

    public static void setBackgroundColorStyle(Node node, String color) {
        node.setStyle("-fx-background-color: " + color + ";");
    }
}
