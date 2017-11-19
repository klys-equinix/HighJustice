package com.algorytmy.Exceptions;

import com.algorytmy.Model.Player;
import com.algorytmy.Model.PlayerExecutable;
import lombok.Data;

/**
 * Created by Konrad Łyś on 19.11.2017 for usage in judge.
 */
@Data
public class ExecutionExcpetion extends Exception {
    private PlayerExecutable guilty;
    public ExecutionExcpetion(String message, PlayerExecutable guiltyPlayer) {
        super();
        this.guilty = guiltyPlayer;
    }
}
