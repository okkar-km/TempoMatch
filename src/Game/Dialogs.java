package Game;

import javax.swing.*;

public class Dialogs {
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