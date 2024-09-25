package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ButtonActionListener implements ActionListener {
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
            controller.showWinDialog(controller.getWindow(), controller.getElapsedTime());
        }
        lastDisabledButton = button;
    }
}