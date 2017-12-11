package com.algorytmy;

import com.algorytmy.Exceptions.ExecutionException;
import com.algorytmy.Model.Match;
import com.algorytmy.Services.AutoGameRunner;
import com.algorytmy.Services.GameService;
import com.algorytmy.Services.LoaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JudgeApplicationTests {

    @Autowired
    LoaderService loaderService;

    @Autowired
    GameService gameService;

    @Autowired
    AutoGameRunner autoGameRunner;

    @Value(value = "classpath:Players")
    private Resource playersFolder;

    @Autowired
    private ArrayList<Match> possibleMatches;

    /*
    I know it is not a valid unit test, just leave it be
     */
    @Test
    public void canRunBasicGame() throws IOException, ExecutionException {
        loaderService.loadPlayers(playersFolder.getFile());
//        gameService.createGame(possibleMatches.get(0));
//        while(gameService.nextMove() != null) {}
//        gameService.getCurrentMatch().toString();
//        gameService.endMatch();
        autoGameRunner.runAllGames();
    }

}
