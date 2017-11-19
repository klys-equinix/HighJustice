package com.algorytmy.Configuration;


import com.algorytmy.Model.Match;
import com.algorytmy.Model.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 */
@Configuration
public class JudgeConfig {
    @Bean
    public Map<String, Player> executablePlayers() {
        return new HashMap<>();
    }

    @Bean
    public ArrayList<Match> possibleGames() {
        return new ArrayList<Match>();
    }
}
