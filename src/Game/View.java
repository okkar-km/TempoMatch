package Game;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private final JButton restartButton;
    private final JLabel stopwatchLabel;

    public View(Model model) {
        this.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        int columns = model.getColumns();
        imagePanel.setLayout(new GridLayout(columns, columns));

        for (JButton button : model.getButtons()) {
            imagePanel.add(button);
        }

        this.add(imagePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        restartButton = new JButton("Restart Game");
        controlPanel.add(restartButton, BorderLayout.CENTER);

        stopwatchLabel = new JLabel("Time: 00:00:00", JLabel.CENTER);
        controlPanel.add(stopwatchLabel, BorderLayout.NORTH);

        this.add(controlPanel, BorderLayout.SOUTH);
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public JLabel getStopwatchLabel() {
        return stopwatchLabel;
    }
}