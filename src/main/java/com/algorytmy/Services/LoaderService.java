package com.algorytmy.Services;

import com.algorytmy.Model.Match;
import com.algorytmy.Model.PlayerExecutable;
import com.algorytmy.Model.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 * This service initially loads player executables and parses info.txt files
 */
@Service
public class LoaderService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    private ArrayList<PlayerExecutable> playerExecutables;

    @Autowired
    private ArrayList<Match> possibleMatches;

    public void loadPlayers(File playersFolder) {

    }

    /**
     * This method will list all possible games in temporary storage - only completed games should be persisted
     */
    public void loadPossibleGames() {

    }
}
