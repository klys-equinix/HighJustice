package com.algorytmy.Services;

import com.algorytmy.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 */
@Service
public class GameService {

    @Autowired
    GameInfoRepository gameInfoRepository;

    @Autowired
    ArrayList<PlayerExecutable> playerExecutables;

    private Game currentGame;


    /**
     * Create game for GUI mode - you are expected to call nextMove to progress the game.
     * @return
     */
    public Game createGame(Game possibleGame) {
        return currentGame;
    }

    /**
     * Call this for automatic mode - all game will be run concurrently, without communicating with GUI.
     * Only final statistics are to be displayed.
      */
    public void runAllGames() {

    }

    /**
     * If this returns null, it means the game has ended
     * @return
     */
    public Move nextMove(Move move) {
        return null;
    }


    public Game getCurrentGame() {
        return currentGame;
    }
}
