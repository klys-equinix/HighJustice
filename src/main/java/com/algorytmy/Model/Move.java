package com.algorytmy.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Data
@AllArgsConstructor
public class Move {
    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;
    private Player player;
    public String toString() {
        return x1 + "x" + y1 + "_" + x2 + "x" + y2;
    }
    public Move(String move, Player player) {
        String[] coordinates = move.split("x|_");
        x1 = Integer.valueOf(coordinates[0]);
        y1 = Integer.valueOf(coordinates[1]);
        x2 = Integer.valueOf(coordinates[2]);
        y2 = Integer.valueOf(coordinates[3]);
        this.player = player;
    }

}
