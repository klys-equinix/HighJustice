package com.algorytmy.Exceptions;

import com.algorytmy.Model.Player;
import lombok.Data;

/**
 * Created by Konrad Łyś on 19.11.2017 for usage in judge.
 */
@Data
public class ExecutionExcepetion extends Exception {
    private Player guilty;
    public ExecutionExcepetion(String message, Player guiltyPlayer) {
        super();
        this.guilty = guiltyPlayer;
    }
}
