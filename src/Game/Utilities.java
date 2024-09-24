package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

public class Utilities {
    public static void timer(int delay, ActionListener listener) {
        Timer t = new Timer(delay, listener);
        t.setRepeats(false);
        t.start();
    }

    public static Image loadImage(String s) {
        Image image = null;
        try {
            InputStream resourceStream = Utilities.class.getResourceAsStream("/" + s);
            if (resourceStream != null) {
                image = ImageIO.read(resourceStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}