package org.example.gui.view;

import org.example.gui.model.DarkChessModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.*;

public class DarkChessFrame extends JFrame {
    private final DarkChessModel.ChessGame game;
    private final JButton[] boardButtons = new JButton[32];
    private final JLabel turnLabel = new JLabel();
    private final JLabel holdLabel = new JLabel();
    private final JLabel messageLabel = new JLabel("Click a cell to play.");
    private boolean boardLocked = false;

    public DarkChessFrame(DarkChessModel.ChessGame game) {
        this.game = game;
        setTitle("Chinese Dark Chess - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        turnLabel.setHorizontalAlignment(SwingConstants.LEFT);
        holdLabel.setHorizontalAlignment(SwingConstants.LEFT);
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        infoPanel.add(turnLabel);
        infoPanel.add(holdLabel);
        infoPanel.add(messageLabel);
        add(infoPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(4, 8, 4, 4));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);

        for (int i = 0; i < 32; i++) {
            final int location = i;
            JButton button = new JButton();
            button.setFont(font);
            button.addActionListener(e -> onCellClicked(location));
            boardButtons[i] = button;
            boardPanel.add(button);
        }

        add(boardPanel, BorderLayout.CENTER);

        refreshBoard();
        setSize(900, 420);
        setLocationRelativeTo(null);
    }

    private void onCellClicked(int location) {
        if (game.gameOver()) {
            boardLocked = true;
            messageLabel.setText(game.getLastMessage());
            setBoardEnabled(false);
            return;
        }

        boolean needSwitch = game.move(location);
        if (!game.getLastMessage().isEmpty()) {
            messageLabel.setText(game.getLastMessage());
        }

        if (needSwitch) {
            game.switchPlayer();
        }

        if (game.gameOver()) {
            boardLocked = true;
            messageLabel.setText(game.getLastMessage());
            setBoardEnabled(false);
        }

        refreshBoard();
    }

    private void refreshBoard() {
        for (int i = 0; i < 32; i++) {
            boardButtons[i].setText(game.getCellText(i));
            boardButtons[i].setToolTipText(toCoordinate(i));
            if (game.getCellSide(i) == 1) {
                boardButtons[i].setForeground(Color.red);
            } else {
                boardButtons[i].setForeground(Color.black);
            }
            if (boardLocked) {
                boardButtons[i].setEnabled(false);
            } else {
                boardButtons[i].setEnabled(game.isCellClickable(i));
            }
        }

        turnLabel.setText("Current turn: " + game.getCurrentPlayerName());
        holdLabel.setText("Hold: " + game.curPlayerHold());
    }

    private void setBoardEnabled(boolean enabled) {
        for (JButton boardButton : boardButtons) {
            boardButton.setEnabled(enabled);
        }
    }

    private String toCoordinate(int location) {
        int row = location / 8;
        int col = location % 8;
        char rowChar = (char) ('A' + row);
        return rowChar + String.valueOf(col + 1);
    }
}

