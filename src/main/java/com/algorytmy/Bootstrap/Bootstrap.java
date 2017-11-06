package com.algorytmy.Bootstrap;

import com.algorytmy.Model.GameInfo;
import com.algorytmy.Model.GameInfoRepository;
import com.algorytmy.Model.Player;
import com.algorytmy.Model.PlayerRepository;
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
    GameInfoRepository gameInfoRepository;

    @Autowired
    PlayerRepository playerRepository;


    @PostConstruct
    public void bootstrap() {
        Player player1 = new Player("Konrad", 0);
        Player player2 = new Player("Szymon", 0);
        playerRepository.save(player1);
        playerRepository.save(player2);
        GameInfo gameInfo = new GameInfo();
        gameInfo.setWinner(player1);
        gameInfo.setLoser(player2);
        gameInfo.setGameEnder(GameInfo.GAME_ENDER.DEFAULT);
        gameInfoRepository.save(gameInfo);

        logger.info(gameInfoRepository.findAll().get(0).toString());
        logger.info(playerRepository.findAll().get(0).toString());
        logger.info(gameInfoRepository.findByWinner_Name("Konrad").toString());
    }
}
