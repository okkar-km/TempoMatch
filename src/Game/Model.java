package Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Model {
    static final String[] DEFAULT_IMAGES = new String[]{"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "10.png", "11.png", "12.png", "13.png", "14.png", "15.png", "16.png", "17.png"};
    final ArrayList<JButton> buttons;
    final int columns;
    boolean gameStarted;

    public Model(int columns) {
        this(columns, null);
    }

    public Model(int columns, List<Image> customImages) {
        this.columns = columns;
        this.buttons = new ArrayList<>();
        this.gameStarted = false;

        int numberOfTiles = columns * columns;
        Vector<Integer> imageIndices = new Vector<>();

        for (int i = 0; i < numberOfTiles / 2; i++) {
            imageIndices.add(i);
            imageIndices.add(i);
        }

        Collections.shuffle(imageIndices);

        for (int i = 0; i < numberOfTiles; i++) {
            int imageIndex = imageIndices.get(i);
            Image image;
            String reference;
            if (customImages != null && imageIndex < customImages.size()) {
                image = customImages.get(imageIndex);
                reference = "custom_" + imageIndex;
            } else {
                reference = DEFAULT_IMAGES[imageIndex % DEFAULT_IMAGES.length];
                image = Utilities.loadImage(reference);
            }

            this.buttons.add(new MemoryButton(image, reference));
        }
    }

    public int getColumns() {
            return columns;
    }
    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public boolean isGameStarted() {
        return this.gameStarted;
    }

    public void startGame() {
        this.gameStarted = true;
    }
}