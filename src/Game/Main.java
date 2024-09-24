package Game;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Main {
    static final int EASY_SIZE = 2;
    static final int MEDIUM_SIZE = 4;
    static final int HARD_SIZE = 6;

    public static void main(String[] args) {
        UIManager.put("OptionPane.background", Color.decode("#4290f5"));
        UIManager.put("Panel.background", Color.decode("#4290f5"));
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller(new Model(MEDIUM_SIZE));
            controller.showDifficultySelectionDialog();
        });
    }
}