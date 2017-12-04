package com.algorytmy.Model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

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

    public Match(Player player1, Player player2, int boardSize) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new FIELD_VALUE[boardSize][boardSize];
        setMatchStatus(MatchStatus.PENDING);
    }
    public void createBoard(Integer size) {
        this.board = new FIELD_VALUE[size][size];
        for (FIELD_VALUE[] row : this.board)
            Arrays.fill(row, FIELD_VALUE.EMPTY);
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
}
