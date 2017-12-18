package com.algorytmy.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "games")
public class MatchResult {

    public enum GAME_ENDER {
        WRONG_INSERTION, TIMEOUT, DEFAULT, CANNOT_EXECUTE
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

    @OneToMany(cascade = CascadeType.ALL)
    List<Move> moveList = new ArrayList<>();

    @NotNull
    private GAME_ENDER gameEnder;

    public MatchResult(Player winner, Player loser, GAME_ENDER gameEnder) {
        this.winner = winner;
        this.loser = loser;
        this.gameEnder = gameEnder;
    }
}
