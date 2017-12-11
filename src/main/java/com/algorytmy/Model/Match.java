package com.algorytmy.Model;

import com.algorytmy.Services.MatchEndListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Data
@NoArgsConstructor
public class Match {
    public enum FIELD_VALUE {
        P1, P2, EMPTY, OBSTACLE
    }
    private FIELD_VALUE[][] board;
    private ArrayList<Move> player1Moves;
    private ArrayList<Move> player2Moves;
    private Player player1;
    private Player player2;
    private MatchResult matchResult;
    private List<MatchEndListener> matchEndListeners = new ArrayList<>();
    public Match(Player player1, Player player2, int boardSize) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new FIELD_VALUE[boardSize][boardSize];
    }
    public void createBoard(Integer size) {
        board = new FIELD_VALUE[size][size];
        for (FIELD_VALUE[] row : this.board)
            Arrays.fill(row, FIELD_VALUE.EMPTY);
        int obstacleRate = size * size / 10; // about 20% of board will be taken by obstacles
        Random random = new Random();
        for(int i = 0; i < obstacleRate; i++) {
            int obstacleLoc = random.nextInt(size - 1);
            if(i % 2 == 0) {
                board[obstacleLoc][obstacleLoc] = FIELD_VALUE.OBSTACLE;
                board[obstacleLoc][obstacleLoc + 1] = FIELD_VALUE.OBSTACLE;
            } else {
                board[obstacleLoc][obstacleLoc] = FIELD_VALUE.OBSTACLE;
                board[obstacleLoc + 1][obstacleLoc] = FIELD_VALUE.OBSTACLE;
            }
        }
    }
    public void addMathEndListener(MatchEndListener matchEndListener) {this.matchEndListeners.add(matchEndListener);}
}
