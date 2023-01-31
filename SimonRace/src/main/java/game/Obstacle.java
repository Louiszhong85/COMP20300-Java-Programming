package game;

import javafx.scene.image.Image;

/**
 * obstacle API
 * The main function is to implement the obstacle class.
 * For example:
 * the image of the obstacle
 * Obstacle Frequency of occurrence
 *
 */
public class Obstacle {


    private Image image;
    private String name;
    /**
     * 出现频率
     * Obstacle Frequency of occurrence
     */
    private int occurrenceRatio;

    public Obstacle(Image image, String name, int occurrenceRatio) {
        this.image = image;
        this.name = name;
        this.occurrenceRatio = occurrenceRatio;
    }

    public Obstacle() {
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOccurrenceRatio() {
        return occurrenceRatio;
    }

    public void setOccurrenceRatio(int occurrenceRatio) {
        this.occurrenceRatio = occurrenceRatio;
    }
}
