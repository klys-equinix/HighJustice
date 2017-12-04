package com.algorytmy.Bootstrap;

import com.algorytmy.GUI.Controller.ComputingWindowController;
import com.algorytmy.JudgeApplication;
import com.algorytmy.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Service
public class Bootstrap {
    final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    @Autowired
    MatchResultRepository matchResultRepository;

    @Autowired
    PlayerRepository playerRepository;


    @PostConstruct
    public void bootstrap() {
        Player player1 = new Player("Konrad", 0);
        Player player2 = new Player("Szymon", 0);
        playerRepository.save(player1);
        playerRepository.save(player2);
        MatchResult matchResult = new MatchResult();
        matchResult.setWinner(player1);
        matchResult.setLoser(player2);
        matchResult.setGameEnder(MatchResult.GAME_ENDER.DEFAULT);
        matchResultRepository.save(matchResult);

        logger.info(matchResultRepository.findAll().get(0).toString());
        logger.info(playerRepository.findAll().get(0).toString());
        logger.info(matchResultRepository.findByWinner_Name("Konrad").toString());
    }
}
