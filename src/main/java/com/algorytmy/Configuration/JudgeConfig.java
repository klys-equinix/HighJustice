package com.algorytmy.Configuration;


import com.algorytmy.Model.Match;
import com.algorytmy.Model.PlayerExecutable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 */
@Configuration
public class JudgeConfig {
    @Bean
    public ArrayList<PlayerExecutable> playerExecutables() {
        return new ArrayList<PlayerExecutable>();
    }

    @Bean
    public ArrayList<Match> possibleGames() {
        return new ArrayList<Match>();
    }
}
