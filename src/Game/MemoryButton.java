package Game;

import javax.swing.*;
import java.awt.*;

public class MemoryButton extends JButton {
    static final Image NO_IMAGE = Utilities.loadImage("no_image.png");

    public MemoryButton(Image image, String reference) {
        Dimension dimension = new Dimension(120, 120);
        this.setPreferredSize(dimension);
        this.setIcon(new ImageIcon(NO_IMAGE));
        this.setDisabledIcon(new ReferencedIcon(image, reference));
    }

    public void markAsMatched() {
        this.setBackground(Color.GREEN);
        this.setOpaque(true);
        this.setBorderPainted(false);
    }
}