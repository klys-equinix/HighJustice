package com.algorytmy.Model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import com.algorytmy.Services.MatchEndListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToOne;
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

    private ObjectProperty<MatchStatus> matchStatusProperty = new SimpleObjectProperty<>();

    private List<MatchEndListener> matchEndListeners = new ArrayList<>();

    public Match(Player player1, Player player2, int boardSize) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new FIELD_VALUE[boardSize][boardSize];
        setMatchStatus(MatchStatus.PENDING);
    }

    public void createBoard(Integer size) {
        board = new FIELD_VALUE[size][size];
        for (FIELD_VALUE[] row : this.board)
            Arrays.fill(row, FIELD_VALUE.EMPTY);
        int obstacleRate = size * size / 20;
        for(int i = 0; i < obstacleRate; i++) {
            Random random = new Random();
            int obstacleLocX = random.nextInt(size - 1);
            int obstacleLocY = random.nextInt(size - 1);
            if(i % 2 == 0) {
                board[obstacleLocX][obstacleLocY] = FIELD_VALUE.OBSTACLE;
                board[obstacleLocX][obstacleLocY + 1] = FIELD_VALUE.OBSTACLE;
            } else {
                board[obstacleLocX][obstacleLocY] = FIELD_VALUE.OBSTACLE;
                board[obstacleLocX + 1][obstacleLocY] = FIELD_VALUE.OBSTACLE;
            }
        }
    }

    public ObjectProperty<MatchStatus> getMatchStatusProperty() {
        return matchStatusProperty;
    }

    public void setMatchStatus(MatchStatus ms) {
        matchStatusProperty.setValue(ms);
    }

    public MatchStatus getMatchStatus() {
        return matchStatusProperty.get();
    }

    @Override
    public String toString() {
        return player1.getName() + " vs " + player2.getName();
    }
    public void addMathEndListener(MatchEndListener matchEndListener) {this.matchEndListeners.add(matchEndListener);}
}
