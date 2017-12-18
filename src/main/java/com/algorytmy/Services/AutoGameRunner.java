package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionException;
import com.algorytmy.Model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
    public void runAllGames(Integer boardSize, File obstacleFile) {
        possibleMatches.forEach(match -> {
            try {
                gameService.createGame(match, boardSize, obstacleFile);
            } catch (ExecutionException executionException) {
                logger.error(executionException.getGuilty().toString());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            while (gameService.nextMove() != null) ;
        });
    }
}
