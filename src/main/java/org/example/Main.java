package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private int side;
    private int location;
    private boolean isTurned;

    public Chess(String _name, int _type, int _side, int _location){
        this.name = _name;
        this.type = _type;
        this.side = _side;
        this.location = _location;
        this.isTurned = true;
    }

    public String showName(){
        return this.name;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public boolean getisTurned(){
        return this.isTurned;
    }
}

class ChessGame extends AbstractGame{
    private Chess[] board = new Chess[32];
    private Player curPlayer;

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
//        for(int i = 0; i < 32; i++){
//            if(i % 8 == 0){
//                System.out.print(rows[i / 8] + "  ");
//            }
//            if(!board[i].getisTurned()){
//                System.out.print("X   ");
//            }
//            else{
//                System.out.print(board[i].showName() + "  ");
//            }
//            if(i % 8 == 7) System.out.println();
//        }
        for (int r = 0; r < 4; r++) {
            System.out.print(rows[r] + " ");
            for (int c = 0; c < 8; c++) {
                int index = r * 8 + c;
                Chess piece = board[index];
                if (!piece.getisTurned()) {
                    System.out.print("X  ");
                } else {
                    System.out.print(piece.showName() + "  ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public boolean move(int location) {
        return false;
    }
}


public class Main {
    public static void main(String[] args) {
        Player p1 = new Player("Alice", 0);
        Player p2 = new Player("Bob", 1);
        ChessGame game = new ChessGame(p1, p2);
        game.showBoard();
    }
}