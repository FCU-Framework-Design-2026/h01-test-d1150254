package org.example.gui;

import org.example.gui.model.DarkChessModel;
import org.example.gui.view.DarkChessFrame;

import javax.swing.SwingUtilities;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DarkChessModel.Player p1 = new DarkChessModel.Player("Alice", -1);
            DarkChessModel.Player p2 = new DarkChessModel.Player("Bob", -1);
            DarkChessModel.ChessGame game = new DarkChessModel.ChessGame(p1, p2);

            DarkChessFrame frame = new DarkChessFrame(game);
            frame.setVisible(true);
        });
    }
}