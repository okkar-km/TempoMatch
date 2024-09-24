package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Game {
    public static class Controller {
        final JFrame window;
        Model model;
        View view;
        Timer stopwatchTimer;
        int elapsedTime;
        boolean stopwatchStarted = false;
        int columns;
        List<Image> customImages;

        public Controller(Model model) {
            this.columns = model.getColumns();
            this.window = new JFrame("Memory");
            this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.window.setResizable(false);
            this.reset(model);
        }

        public void uploadCustomImages() {

            int uploadChoice = JOptionPane.showConfirmDialog(
                window,
                "Do you want to upload custom images?",
                "Custom Images",
                JOptionPane.YES_NO_OPTION
            );

            if (uploadChoice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                    window,
                    "Please upload PNG images of size 120x120 pixels.",
                    "Image Upload Instructions",
                    JOptionPane.INFORMATION_MESSAGE
                );
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));

                int returnValue = fileChooser.showOpenDialog(this.window);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    List<Image> images = new ArrayList<>();
                    for (File file : selectedFiles) {
                        try {
                            Image image = ImageIO.read(file);
                            images.add(image);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(window, "Failed to load image: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (!images.isEmpty()) {
                        showImagePreviews(images);
                        if (images.size() >= columns * columns / 2) {
                            this.customImages = images;
                            reset(new Model(columns, customImages));
                        } else {
                            JOptionPane.showMessageDialog(window, "Not enough images uploaded! Please upload at least " + (columns * columns / 2) + " images.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {

                        JOptionPane.showMessageDialog(window, "No images were uploaded. Using default images.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        reset(new Model(columns));
                    }
                } else {

                    JOptionPane.showMessageDialog(window, "Image upload canceled. Using default images.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    reset(new Model(columns));
                }
            } else {

                reset(new Model(columns));
            }
        }

        public void showImagePreviews(List<Image> images) {

            JDialog previewDialog = new JDialog(window, "Preview Images", true);
            previewDialog.setSize(600, 400);
            previewDialog.setLayout(new BorderLayout());


            JPanel thumbnailPanel = new JPanel();
            thumbnailPanel.setLayout(new GridLayout(2, 2, 10, 10));


            for (Image img : images) {
                JLabel thumbnailLabel = new JLabel();
                thumbnailLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                thumbnailPanel.add(thumbnailLabel);
            }

            previewDialog.add(new JScrollPane(thumbnailPanel), BorderLayout.CENTER);


            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Continue");
            buttonPanel.add(confirmButton);

            confirmButton.addActionListener(_ -> {
                if (images.size() >= columns * columns / 2) {
                    this.customImages = images;
                    reset(new Model(columns, customImages));
                } else {
                    JOptionPane.showMessageDialog(window, "Not enough images uploaded! Please upload at least " + (columns * columns / 2) + " images.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                previewDialog.dispose();
            });

            previewDialog.add(buttonPanel, BorderLayout.SOUTH);
            previewDialog.setLocationRelativeTo(window);
            previewDialog.setVisible(true);
        }

        public void reset(Model model) {
            this.model = model;
            this.view = new View(model);
            this.window.setContentPane(view);
            this.window.pack();
            this.window.setLocationRelativeTo(null);
            this.window.setVisible(true);

            for (JButton button : this.model.getButtons()) {
                button.addActionListener(new ButtonActionListener(this));
            }

            view.getRestartButton().addActionListener(_ -> restartGame());
            Utilities.timer(200, (ignored) -> this.window.setVisible(true));
        }


        public void restartGame() {
            stopStopwatch();
            stopwatchStarted = false;
            showDifficultySelectionDialog();

        }
        public void startStopwatch() {
        if (!stopwatchStarted) {
            elapsedTime = 0;
            stopwatchStarted = true;
            stopwatchTimer = new Timer(1000, _ -> {
                elapsedTime++;
                view.getStopwatchLabel().setText("Time: " + elapsedTime + "s");
            });
            stopwatchTimer.start();
        }
    }

        public void showDifficultySelectionDialog() {
            String[] options = {"Easy", "Medium", "Hard"};
            int selectedOption = JOptionPane.showOptionDialog(null,
                "Select Difficulty",
                "Memory Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

            if (selectedOption == -1) return;

            int gridSize = selectedOption == 0 ? Main.EASY_SIZE :
                       selectedOption == 1 ? Main.MEDIUM_SIZE : Main.HARD_SIZE;

            this.columns = gridSize;

            uploadCustomImages();
        }

        public void onMatch() {
            SoundPlayer.playSound("sounds/90s-game-ui-6-185099.wav");
        }
        public void onMisMatch(){
            SoundPlayer.playSound("sounds/error-8-206492.wav");
        }
        public void onWin(){
            SoundPlayer.playSound("sounds/success-1-6297.wav");
        }

        public void stopStopwatch() {
            if (stopwatchTimer != null) {
                stopwatchTimer.stop();
            }
        }

        public int getElapsedTime() {
            return elapsedTime;
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
        // Constants for the game
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
                    image = customImages.get(imageIndex);  // Use custom image
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

    public static class View extends JPanel {
        private final JButton restartButton;// remove final
        private final JLabel stopwatchLabel;// remove final

        public View(Model model) {
            this.setLayout(new BorderLayout());  // Use BorderLayout instead of BoxLayout

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
            controlPanel.add(restartButton, BorderLayout.CENTER);  // Add the restart button at the bottom

            stopwatchLabel = new JLabel("Time: 0s", JLabel.CENTER);
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


    public static class MemoryButton extends JButton {
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
     public static class ButtonActionListener implements ActionListener {
        final Controller controller;
        final Model model;
        final View view;
        static int disabledButtonCount = 0;
        static JButton lastDisabledButton = null;
        public ButtonActionListener(Controller controller) {
            this.controller = controller;
            this.model = controller.getModel();
            this.view = controller.getView();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);
            ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
            disabledButtonCount++;
            if (!model.isGameStarted()) {
                model.startGame();
                controller.startStopwatch();
            }
            if (disabledButtonCount == 2) {
                ReferencedIcon thatIcon = (ReferencedIcon) lastDisabledButton.getDisabledIcon();
                boolean isPair = thisIcon.getReference().equals(thatIcon.getReference());
                if (isPair) {
                    ((MemoryButton) button).markAsMatched();
                    ((MemoryButton) lastDisabledButton).markAsMatched();
                    controller.onMatch();
                } else {
                    controller.onMisMatch();
                    JButton lastButton = lastDisabledButton;
                    Utilities.timer(500, ((ignored) -> {
                        button.setEnabled(true);
                     lastButton.setEnabled(true);
                    }));
                }
                disabledButtonCount = 0;
            }
            List<JButton> enabledButtons = model.getButtons().stream().filter(Component::isEnabled).toList();
            if (enabledButtons.isEmpty()) {
                controller.onWin();
                controller.stopStopwatch();
                Dialogs.showWinDialog(controller.getWindow(), controller.getElapsedTime());
            }
            lastDisabledButton = button;

        }
    }

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

    public static class Utilities {

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
    public static class Dialogs {
        public static void showWinDialog(JFrame window, int elapsedTime) {
            String message = String.format("Congrats, you won in %d seconds!", elapsedTime);

            JOptionPane.showMessageDialog(
                window.getContentPane(),
                message,
                "You Won!",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}

class Main {
    static final int EASY_SIZE = 2;
    static final int MEDIUM_SIZE = 4;
    static final int HARD_SIZE = 6;

    public static void main(String[] args) {
        UIManager.put("OptionPane.background", Color.decode("#4290f5"));
        UIManager.put("Panel.background", Color.decode("#4290f5"));
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> {
            Game.Controller controller = new Game.Controller(new Game.Model(Main.MEDIUM_SIZE));
            controller.showDifficultySelectionDialog();
        });
    }
}