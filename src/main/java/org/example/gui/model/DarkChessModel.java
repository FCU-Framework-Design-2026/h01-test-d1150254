package org.example.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DarkChessModel {
    public static class Player {
        String name;
        int side;

        public Player(String name, int side) {
            this.name = name;
            this.side = side;
        }
    }

    public abstract static class AbstractGame {
        protected Player player1;
        protected Player player2;

        public void setPlayer(Player p1, Player p2) {
            this.player1 = p1;
            this.player2 = p2;
        }

        public abstract boolean gameOver();

        public abstract boolean move(int location);
    }

    public static class Chess {
        private final String name;
        private final int type;
        private final int side;
        private int location;
        private boolean isTurned;

        public Chess(String name, int type, int side, int location) {
            this.name = name;
            this.type = type;
            this.side = side;
            this.location = location;
            this.isTurned = false;
        }

        public String getName() {
            return this.name;
        }

        public void setLocation(int location) {
            this.location = location;
        }

        public void setisTurned() {
            this.isTurned = true;
        }

        public boolean getisTurned() {
            return this.isTurned;
        }

        public int getSide() {
            return this.side;
        }

        public int getLocation() {
            return location;
        }

        public int getType() {
            return this.type;
        }
    }

    public static class ChessGame extends AbstractGame {
        private final Chess[] board = new Chess[32];
        private Player curPlayer;
        private int selectLocation = -1;
        private String lastMessage = "";

        private final String[] chessName = {
                "將", "士", "士", "象", "象", "車", "車", "馬", "馬", "包", "包", "卒", "卒", "卒", "卒", "卒",
                "帥", "仕", "仕", "相", "相", "俥", "俥", "傌", "傌", "炮", "炮", "兵", "兵", "兵", "兵", "兵"
        };

        private final int[] chessType = {
                7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 1, 1, 1
        };

        public ChessGame(Player p1, Player p2) {
            setPlayer(p1, p2);
            this.curPlayer = p1;
            generateChess();
        }

        private void generateChess() {
            List<Chess> temp = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                temp.add(new Chess(chessName[i], chessType[i % 16], i / 16, -1));
            }
            Collections.shuffle(temp);
            for (int i = 0; i < 32; i++) {
                board[i] = temp.get(i);
                board[i].setLocation(i);
            }
        }

        public void switchPlayer() {
            curPlayer = curPlayer == player1 ? player2 : player1;
        }

        public String getCurrentPlayerName() {
            return curPlayer.name;
        }

        public String curPlayerHold() {
            if (selectLocation != -1 && board[selectLocation] != null) {
                return board[selectLocation].getName();
            }
            return "Nothing";
        }

        public String getCellText(int location) {
            Chess chess = board[location];
            if (chess == null) {
                return "＿";
            }
            if (!chess.getisTurned()) {
                return "X";
            }
            return chess.getName();
        }

        public int getCellSide(int location) {
            Chess chess = board[location];
            if (chess == null || !chess.getisTurned()) {
                return -1;
            }
            return chess.getSide();
        }

        public boolean hasSelectedChess() {
            return selectLocation != -1;
        }

        public boolean isCellClickable(int location) {
            if (location < 0 || location >= 32) {
                return false;
            }

            if (selectLocation == -1) {
                return true;
            }

            Chess movingChess = board[selectLocation];
            if (movingChess == null) {
                return true;
            }

            Chess targetChess = board[location];
            if (targetChess != null && targetChess.getisTurned() && targetChess.getSide() == curPlayer.side) {
                return true;
            }

            if (targetChess != null) {
                return isValidMove(movingChess, targetChess);
            }

            return isAdjacent(movingChess.getLocation(), location);
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public void clearLastMessage() {
            lastMessage = "";
        }

        @Override
        public boolean gameOver() {
            int blackChess = 0;
            int redChess = 0;
            for (Chess c : board) {
                if (c == null) {
                    continue;
                }
                if (c.getSide() == 0) {
                    blackChess++;
                } else {
                    redChess++;
                }
            }
            if (blackChess == 0) {
                Player winner = (player1.side == 1) ? player1 : player2;
                lastMessage = winner.name + " wins!";
                return true;
            } else if (redChess == 0) {
                Player winner = (player1.side == 0) ? player1 : player2;
                lastMessage = winner.name + " wins!";
                return true;
            }
            return false;
        }

        @Override
        public boolean move(int location) {
            clearLastMessage();
            if (location < 0 || location >= 32) {
                lastMessage = "Invalid location!";
                return false;
            }

            Chess targetChess = board[location];

            if (selectLocation == -1) {
                if (targetChess == null) {
                    lastMessage = "No chess at this location!";
                    return false;
                }
                if (!targetChess.getisTurned()) {
                    targetChess.setisTurned();
                    if (curPlayer.side == -1) {
                        curPlayer.side = targetChess.getSide();
                        player2.side = targetChess.getSide() == 1 ? 0 : 1;
                        lastMessage = player1.name + "is " + (curPlayer.side == 0 ? "黑棋" : "紅棋");
                    }
                    return true;
                }
                if (targetChess.getSide() != curPlayer.side) {
                    lastMessage = "This chess belongs to the opponent!";
                    return false;
                }
                selectLocation = location;
                lastMessage = curPlayer.name + " choose " + targetChess.getName();
                return false;
            } else {
                Chess movingChess = board[selectLocation];
                if (targetChess != null && targetChess.getisTurned() && targetChess.getSide() == curPlayer.side) {
                    selectLocation = location;
                    lastMessage = curPlayer.name + " choose " + targetChess.getName();
                    return false;
                }
                if (targetChess != null && isValidMove(movingChess, targetChess)) {
                    lastMessage = movingChess.getName() + "eat" + targetChess.getName();

                    board[location] = movingChess;
                    movingChess.setLocation(location);
                    board[selectLocation] = null;
                    selectLocation = -1;
                    return true;
                } else if (targetChess == null) {
                    int row1 = movingChess.getLocation() / 8;
                    int col1 = movingChess.getLocation() % 8;
                    int row2 = location / 8;
                    int col2 = location % 8;
                    int distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);
                    if (distance != 1) {
                        selectLocation = -1;
                        lastMessage = "Invalid move!";
                        return false;
                    }
                    lastMessage = movingChess.getName() + "move success";
                    board[location] = movingChess;
                    movingChess.setLocation(location);
                    board[selectLocation] = null;
                    selectLocation = -1;
                    return true;
                } else {
                    selectLocation = -1;
                    lastMessage = "Invalid move!";
                    return false;
                }
            }
        }

        private boolean isValidMove(Chess movingChess, Chess targetChess) {
            if (!targetChess.getisTurned() || movingChess.getSide() == targetChess.getSide()
                    || movingChess.getLocation() == targetChess.getLocation()) {
                return false;
            }

            int row1 = movingChess.getLocation() / 8;
            int col1 = movingChess.getLocation() % 8;
            int row2 = targetChess.getLocation() / 8;
            int col2 = targetChess.getLocation() % 8;
            int distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);

            if (movingChess.getType() == 2) {
                int count = 0;
                if (row1 == row2) {
                    for (int i = Math.min(col1, col2) + 1; i < Math.max(col1, col2); i++) {
                        if (board[row1 * 8 + i] != null) {
                            count++;
                        }
                    }
                } else if (col1 == col2) {
                    for (int i = Math.min(row1, row2) + 1; i < Math.max(row1, row2); i++) {
                        if (board[i * 8 + col1] != null) {
                            count++;
                        }
                    }
                }
                return count == 1;
            }

            if (distance != 1) {
                return false;
            }
            if (movingChess.getType() == 7 && targetChess.getType() == 1) {
                return false;
            }
            if (movingChess.getType() == 1 && targetChess.getType() == 7) {
                return true;
            }
            return movingChess.getType() >= targetChess.getType();
        }

        private boolean isAdjacent(int from, int to) {
            int row1 = from / 8;
            int col1 = from % 8;
            int row2 = to / 8;
            int col2 = to % 8;
            return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
        }
    }
}

