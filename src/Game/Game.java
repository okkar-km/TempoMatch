package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.Timer;

public class Game {

    public static class Controller {
        final JFrame window;
        Model model;
        View view;

        // Constructor
        public Controller(Model model) {
            this.window = new JFrame("Memory"); // Create the window
            this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // Close the window when the user clicks on the close button
            this.window.setResizable(false); // Disable the resizing of the window
            this.reset(model); // Reset the game
        }

        // Reset the game
        public void reset(Model model) {
            this.model = model;
            this.view = new View(model);
            this.window.setVisible(false);
            this.window.setContentPane(view);
            this.window.pack();
            this.window.setLocationRelativeTo(null);
            for (JButton button : this.model.getButtons()) {
                button.addActionListener(new ButtonActionListener(this));
            }
            Utilities.timer(200, (ignored) -> this.window.setVisible(true)); // Show the window after 200ms
        }

        public JFrame getWindow() {
            return this.window;
        }

        public Model getModel() {
            return this.model;
        }

        public View getView() {
            return this.view;
        }
    }

    public static class Model {
        static final String[] AVAILABLE_IMAGES = {"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png"};
        final ArrayList<JButton> buttons;
        final int columns; // Number of columns (grid size)
        int tries; // Number of tries
        final int maxTries; // Max number of tries
        boolean gameStarted; // Is the game started?

        public Model(int columns, int maxTries) {
            this.columns = columns;
            this.maxTries = maxTries; // Set max tries based on difficulty
            this.buttons = new ArrayList<>();
            this.tries = maxTries; // Set initial tries
            this.gameStarted = false;

            int numberOfImage = columns * columns;
            Vector<Integer> v = new Vector<>();
            for (int i = 0; i < numberOfImage - numberOfImage % 2; i++) {
                v.add(i % (numberOfImage / 2));
            }
            if (numberOfImage % 2 != 0) v.add(AVAILABLE_IMAGES.length - 1);
            for (int i = 0; i < numberOfImage; i++) {
                int rand = (int) (Math.random() * v.size());
                String reference = AVAILABLE_IMAGES[v.elementAt(rand)];
                this.buttons.add(new MemoryButton(reference));
                v.removeElementAt(rand);
            }
        }

        public int getColumns() {
            return columns;
        }

        public ArrayList<JButton> getButtons() {
            return buttons;
        }

        public int getTries() {
            return tries;
        }

        public void decrementTries() {
            this.tries--;
        }

        public boolean isGameStarted() {
            return this.gameStarted;
        }

        public void startGame() {
            this.gameStarted = true;
        }

        public int getMaxTries() {
            return maxTries;
        }
    }

    // class to handle the UI of the game
    public static class View extends JPanel {
        final JLabel triesLabel;

        public View(Model model) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.triesLabel = new JLabel("", SwingConstants.CENTER);
            this.triesLabel.setFont(new Font("MV Boli", Font.BOLD, 30));
            this.triesLabel.setForeground(Color.WHITE);

            JPanel imagePanel = new JPanel();
            int columns = model.getColumns();
            imagePanel.setLayout(new GridLayout(columns, columns));
            for (JButton button : model.getButtons()) {
                imagePanel.add(button);
            }

            setTries(model.getTries());

            JPanel triesPanel = new JPanel();
            triesPanel.add(this.triesLabel);
            triesPanel.setAlignmentX(CENTER_ALIGNMENT);
            triesPanel.setBackground(new Color(0X8946A6));
            this.add(triesPanel);
            this.add(imagePanel);
        }

        public void setTries(int triesLeft) {
            this.triesLabel.setText("Tries left: " + triesLeft);
        }
    }

    // class to handle the button clicks
    public static class ReferencedIcon extends ImageIcon {
        final String reference;

        public ReferencedIcon(Image image, String reference) {
            super(image);
            this.reference = reference;
        }

        public String getReference() {
            return reference;
        }
    }

    // class to handle the button on the images
    public static class MemoryButton extends JButton {
        static final String IMAGE_PATH = "";
        static final Image NO_IMAGE = Utilities.loadImage("no_image.png");

        public MemoryButton(String reference) {
            Image image = Utilities.loadImage(IMAGE_PATH + reference);
            Dimension dimension = new Dimension(120, 120);
            this.setPreferredSize(dimension);
            this.setIcon(new ImageIcon(NO_IMAGE));
            this.setDisabledIcon(new ReferencedIcon(image, reference));
        }
    }

    public static class Dialogs {
        public static void showLoseDialog(JFrame window) {
            UIManager.put("OptionPane.background", new Color(0XEA99D5));
            UIManager.put("Panel.background", new Color(0XEA99D5));
            JOptionPane.showMessageDialog(window, "You lost, try again!", "You lost!", JOptionPane.INFORMATION_MESSAGE);
        }

        public static void showWinDialog(JFrame window, Model model) {
            String message = String.format("Congrats you won!!");
            UIManager.put("OptionPane.background", new Color(0XEA99D5));
            UIManager.put("Panel.background", new Color(0XEA99D5));
            JOptionPane.showMessageDialog(window.getContentPane(), message, "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // class to handle the button clicks
    public static class ButtonActionListener implements ActionListener {
        final Controller controller;
        final Model model;
        final View view;
        final JFrame window;
        static int disabledButtonCount = 0;
        static JButton lastDisabledButton = null;
        static final Image TRAP_IMAGE = Utilities.loadImage("no_image.png");
        final ReferencedIcon trap;

        public ButtonActionListener(Controller controller) {
            this.controller = controller;
            this.model = controller.getModel();
            this.view = controller.getView();
            this.window = controller.getWindow();
            this.trap = new ReferencedIcon(TRAP_IMAGE, "no_image.png");
        }

        // Method to handle the button clicks and check if two images are same
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);
            ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
            disabledButtonCount++;
            if (!model.isGameStarted()) { // If the game has not started
                model.startGame(); // Start the game
            }
            if (disabledButtonCount == 2) { // If two buttons are disabled
                ReferencedIcon thatIcon = (ReferencedIcon) lastDisabledButton.getDisabledIcon();
                boolean isPair = thisIcon.getReference().equals(thatIcon.getReference()); // Check if the two images are the same
                if (!isPair) { // If the two images are not the same
                    model.decrementTries(); // Decrement the number of tries
                    view.setTries(model.getTries()); // Update the number of tries
                    JButton lastButton = lastDisabledButton; // Store the last button
                    Utilities.timer(500, ((ignored) -> { // Wait 500ms before re-enabling the buttons
                        button.setEnabled(true); // Re-enable the button
                        lastButton.setEnabled(true); // Re-enable the last button
                    }));
                }
                disabledButtonCount = 0; // Reset the counter
            }
            ArrayList<JButton> enabledButtons = (ArrayList<JButton>) model.getButtons().stream().filter(Component::isEnabled).collect(Collectors.toList());
            if (enabledButtons.size() == 0) { // If all the buttons are disabled
                controller.reset(new Model(controller.getModel().getColumns(), controller.getModel().getMaxTries())); // Reset the game
                Dialogs.showWinDialog(window, model); // Show the win dialog
            } else if (model.getTries() == 0) { // If there are no more tries
                controller.reset(new Model(controller.getModel().getColumns(), controller.getModel().getMaxTries())); // Reset the game
                Dialogs.showLoseDialog(window); // Show the lose dialog
            }
            lastDisabledButton = button; // Store the last button
        }
    }

    public static class Utilities {
        public static Image loadImage(String path) {
            try {
                return ImageIO.read(new File(path));
            } catch (IOException e) {
                return null;
            }
        }

        // Method to handle a delay in execution
        public static void timer(int milliseconds, ActionListener listener) {
            Timer timer = new Timer(milliseconds, listener);
            timer.setRepeats(false); // Disable repeats so that the timer only fires once
            timer.start();
        }
    }
}
class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Easy", "Medium", "Hard"};
            int response = JOptionPane.showOptionDialog(
                    null,
                    "Select Difficulty Level",
                    "Memory Game",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            int gridSize = 4; // Default grid size (easy mode)
            int maxTries = 10; // Default number of tries

            switch (response) {
                case 0: // Easy
                    gridSize = 4; // 4x4 grid
                    maxTries = 15; // More tries
                    break;
                case 1: // Medium
                    gridSize = 6; // 6x6 grid
                    maxTries = 10;
                    break;
                case 2: // Hard
                    gridSize = 8; // 8x8 grid
                    maxTries = 5; // Least tries
                    break;
                default:
                    System.exit(0); // Exit if no difficulty is selected
            }

            new Game.Controller(new Game.Model(gridSize, maxTries));
        });
    }
}
