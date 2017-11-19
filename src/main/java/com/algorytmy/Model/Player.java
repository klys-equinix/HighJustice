package com.algorytmy.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "players")
public class Player {

    @NotNull
    private Integer score;

    @Id
    @NotNull
    private String name;

    @Transient
    PlayerExecutable playerExecutable;

    @Transient
    Match.FIELD_VALUE playerSignature;

    public Player(String name, Integer score) {
        this.score = score;
        this.name = name;
    }

    public void addToScore() {
        this.score += 1;
    }
}
