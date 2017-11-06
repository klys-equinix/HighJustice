package com.algorytmy.Model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Entity
@Data
@Table(name = "games")
public class GameInfo {

    public enum GAME_ENDER {
            WRONG_INSERTION, TIMEOUT, DEFAULT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Player winner;

    @ManyToOne
    @NotNull
    private Player loser;

    @NotNull
    private GAME_ENDER gameEnder;
}
