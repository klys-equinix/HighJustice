package com.algorytmy.Exceptions;

import com.algorytmy.Model.Player;
import com.algorytmy.Model.PlayerExecutable;
import lombok.Data;

/**
 * Created by Konrad Łyś on 19.11.2017 for usage in judge.
 */
@Data
public class ExecutionExcpetion extends Exception {
    private Player guilty;
    public ExecutionExcpetion(String message, Player guiltyPlayer) {
        super();
        this.guilty = guiltyPlayer;
    }
}
