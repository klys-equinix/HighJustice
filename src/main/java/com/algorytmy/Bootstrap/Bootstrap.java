package com.algorytmy.Bootstrap;

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
    }
}
