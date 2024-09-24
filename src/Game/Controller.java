package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Controller {
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