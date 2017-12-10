package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionExcepetion;
import com.algorytmy.Model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by Konrad Łyś on 24.11.2017 for usage in judge.
 */
@Service
public class AutoGameRunner {
    final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private ArrayList<Match> possibleMatches;

    @Autowired
    private GameService gameService;

    /**
     * Call this for automatic mode - all games will be run concurrently, without communicating with GUI.
     * Only final statistics are to be displayed.
     * Single threaded for now because of issues.
     */
    public void runAllGames() {
        possibleMatches.forEach(match -> {
            try {
                gameService.createGame(match);
            } catch (ExecutionExcepetion executionExcepetion) {
                logger.error(executionExcepetion.getGuilty().toString());
            }
            while(gameService.nextMove() != null);
            gameService.endMatch();
        });
    }
}
