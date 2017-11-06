package com.algorytmy.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Data
@NoArgsConstructor
public class Game {
    private Integer[][] board;
    private ArrayList<Move> player1Moves;
    private ArrayList<Move> player2Moves;
    private Player player1;
    private Player player2;
    private GameInfo gameInfo;
    public Game(Player player1, Player player2, int boardSize) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Integer[boardSize][boardSize];
    }
}
