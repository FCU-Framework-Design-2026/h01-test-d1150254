package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Player{
    String name;
    int side;

    public Player(String _name, int _side){
        this.name = _name;
        this.side = _side;
    }
}

abstract class AbstractGame{
    protected Player player1;
    protected Player player2;

    public void setPlayer(Player p1, Player p2){
        this.player1 = p1;
        this.player2 = p2;
    }
    public abstract boolean gameOver();
    public abstract boolean move(int location);
}

class Chess{
    private String name;
    private int type;
    private int side;           //0 for black, 1 for red
    private int location;
    private boolean isTurned;

    public Chess(String _name, int _type, int _side, int _location){
        this.name = _name;
        this.type = _type;
        this.side = _side;
        this.location = _location;
        this.isTurned = false;
    }

    public String getName(){
        return this.name;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setisTurned() {this.isTurned = true;}

    public boolean getisTurned(){
        return this.isTurned;
    }

    public int getSide() {return this.side;}

    public int getLocation() {
        return location;
    }

    public int getType(){
        return this.type;
    }
}

class ChessGame extends AbstractGame{
    private Chess[] board = new Chess[32];
    private Player curPlayer;
    private int selectLocation = -1;

    private String[] chessName = {
            "將", "士", "士", "象", "象", "車", "車", "馬", "馬", "包", "包", "卒", "卒", "卒", "卒", "卒",
            "帥", "仕", "仕", "相", "相", "俥", "俥", "傌", "傌", "炮", "炮", "兵", "兵", "兵", "兵", "兵"
    };

    private int[] chessType = {
            7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 1, 1, 1
    };

    public ChessGame(Player p1, Player p2){
        setPlayer(p1, p2);
        this.curPlayer = p1;
        generateChess();
    }

    private void generateChess(){
        List<Chess> temp = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            temp.add(new Chess(chessName[i], chessType[i % 16], i / 16, -1));
        }
        Collections.shuffle(temp);
        for(int i = 0; i < 32; i++){
            board[i] = temp.get(i);
            board[i].setLocation(i);
        }
    }

    public void showBoard(){
        System.out.println("   1   2   3  4   5  6   7   8");
        char[] rows = {'A', 'B', 'C', 'D'};
        for(int i = 0; i < 32; i++){
            if(i % 8 == 0){
                System.out.print(rows[i / 8] + "  ");
            }
            if(board[i] == null){
                System.out.print("_  ");
            }
            else if(!board[i].getisTurned()){
                System.out.print("X   ");
            }
            else{
                if(board[i].getSide() == 1){
                    System.out.print("\u001B[31m" + board[i].getName() + "  " + "\u001B[0m");
                }
                else{
                    System.out.print(board[i].getName() + "  ");
                }
            }
            if(i % 8 == 7) System.out.println();
        }
    }

    public void switchPlayer(){
        curPlayer = curPlayer == player1 ? player2 : player1;
    }

    public String getCurrentPlayerName(){
        return curPlayer.name;
    }

    public String curPlayerHold(){
        if(selectLocation != -1){
            return board[selectLocation].getName();
        }
        return "Nothing";
    }

    @Override
    public boolean gameOver() {
        int blackChess = 0, redChess = 0;
        for(Chess c : board){
            if(c == null) continue;
            if(c.getSide() == 0) blackChess++;
            else redChess++;
        }
        if(blackChess == 0){
            Player winner = (player1.side == 1) ? player1 : player2;
            System.out.println(winner.name + " wins!");
            return true;
        }
        else if(redChess == 0){
            Player winner = (player1.side == 0) ? player1 : player2;
            System.out.println(winner.name + " wins!");
            return true;
        }
        return false;
    }

    @Override
    public boolean move(int location) {
        if(location < 0 || location >= 32){
            System.out.println("Invalid location!");
            return false;
        }

        Chess targetChess = board[location];

        if(selectLocation == -1){
            if(targetChess == null){
                System.out.println("No chess at this location!");
                return false;
            }
            if(!targetChess.getisTurned()){
                targetChess.setisTurned();
                if(curPlayer.side == -1) {
                    curPlayer.side = targetChess.getSide();
                    player2.side = targetChess.getSide() == 1 ? 0 : 1;
                    System.out.println(player1.name + "is " + (curPlayer.side == 0 ? "黑棋" : "紅棋"));
                }
                return true;
            }
            if(targetChess.getSide() != curPlayer.side){
                System.out.println("This chess belongs to the opponent!");
                return false;
            }
            selectLocation = location;
            System.out.println(curPlayer.name + " choose " + targetChess.getName());
            return false;
        }
        else{
            Chess movingChess = board[selectLocation];
            if(targetChess != null && targetChess.getisTurned() && targetChess.getSide() == curPlayer.side){
                selectLocation = location;
                System.out.println(curPlayer.name + " choose " + targetChess.getName());
                return false;
            }
            if(targetChess != null && isValidMove(movingChess, targetChess)){
                System.out.println(movingChess.getName() + "eat" + targetChess.getName());

                board[location] = movingChess;
                movingChess.setLocation(location);
                board[selectLocation] = null;
                selectLocation = -1;
                return true;
            }
            else if(targetChess == null){
                int row1 = movingChess.getLocation() / 8, col1 = movingChess.getLocation() % 8;
                int row2 = location / 8, col2 = location % 8;
                int distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);
                if(distance != 1){
                    selectLocation = -1;
                    System.out.println("Invalid move!");
                    return false;
                }
                System.out.println(movingChess.getName() + "move success");
                board[location] = movingChess;
                movingChess.setLocation(location);
                board[selectLocation] = null;
                selectLocation = -1;
                return true;
            }
            else {
                selectLocation = -1;
                System.out.println("Invalid move!");
                return false;
            }
        }
    }

    private boolean isValidMove(Chess movingChess, Chess targetChess){
        if(!targetChess.getisTurned() || movingChess.getSide() == targetChess.getSide() || movingChess.getLocation() == targetChess.getLocation()){
            return false;
        }

        int row1 = movingChess.getLocation() / 8, col1 = movingChess.getLocation() % 8;
        int row2 = targetChess.getLocation() / 8, col2 = targetChess.getLocation() % 8;
        int distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);

        if(movingChess.getType() == 2){
            int count = 0;
            if(row1 == row2){
                for(int i = Math.min(col1, col2) + 1; i < Math.max(col1, col2); i++){
                    if(board[row1 * 8 + i] != null) count++;
                }
            }
            else if(col1 == col2){
                for(int i = Math.min(row1, row2) + 1; i < Math.max(row1, row2); i++){
                    if(board[i * 8 + col1] != null) count++;
                }
            }
            return count == 1;
        }

        if(distance != 1) return false;
        if(movingChess.getType() == 7 && targetChess.getType() == 1) return false;
        if(movingChess.getType() == 1 && targetChess.getType() == 7) return true;
        return movingChess.getType() >= targetChess.getType();
    }
}


public class Main {
    public static void main(String[] args) {
        Player p1 = new Player("Alice", -1);
        Player p2 = new Player("Bob", -1);
        ChessGame game = new ChessGame(p1, p2);
        try (Scanner scanner = new Scanner(System.in)) {
            while(!game.gameOver()){
                game.showBoard();
                System.out.println(game.getCurrentPlayerName() + "'s turn. " + "hold " + game.curPlayerHold());
                System.out.println("input lacation:");

                if(!scanner.hasNextLine()){
                    System.out.println("Input ended. Game terminated.");
                    break;
                }
                String input = scanner.nextLine().toUpperCase();

                if(input.length() != 2){
                    System.out.println("Invalid input!");
                    continue;
                }
                int row = input.charAt(0) - 'A';
                int col = input.charAt(1) - '1';
                if(row < 0 || row >= 4 || col < 0 || col >= 8){
                    System.out.println("Invalid input!");
                    continue;
                }
                int location = row * 8 + col;
                if(game.move(location)){
                    game.switchPlayer();
                }
            }
        }
    }
}