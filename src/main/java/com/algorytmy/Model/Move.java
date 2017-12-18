package com.algorytmy.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Entity
@Data
@AllArgsConstructor
public class Move {
    @Id
    @GeneratedValue
    private Long id;

    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;
    @ManyToOne
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
