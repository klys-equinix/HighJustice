package com.algorytmy.Services;

import com.algorytmy.Model.Match;
import com.algorytmy.Model.Player;
import com.algorytmy.Model.PlayerExecutable;
import com.algorytmy.Model.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 * This service initially loads player executables and parses info.txt files
 */
@Service
public class LoaderService {
    final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    private Map<String, Player> playerExecutables;

    @Autowired
    private ArrayList<Match> possibleMatches;

    public void loadPlayers(File playersFolder) {
        File[] directories = playersFolder.listFiles(file -> file.isDirectory());
        Arrays.stream(directories).forEach(directory -> parseDirectory(directory));
        loadPossibleGames();
    }

    /**
     * This method will list all possible games in temporary storage - only completed games should be persisted
     */
    private void loadPossibleGames() {
        playerExecutables.forEach((playerName, player) -> {
            playerExecutables.forEach((playerName1, player1) -> {
                if (!playerName.equals(playerName1)) {
                    Match match = new Match();
                    match.setPlayer1(player);
                    match.setPlayer2(player1);
                    possibleMatches.add(match);
                }
            });
        });

    }

    private void parseDirectory(File directory) {
        File[] files = directory.listFiles();
        Player player = new Player();
        PlayerExecutable playerExecutable = new PlayerExecutable();
        String command = "";
        String path = "";
        for (File file : files) {
            if (file.getName().equals("info.txt")) {
                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    player.setName(fileReader.readLine());
                    command = fileReader.readLine();
                } catch (FileNotFoundException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            } else {
                path = file.getAbsolutePath();
            }
        }
        playerExecutable.setCommandLineExecution(command + " " + path);
        player.setPlayerExecutable(playerExecutable);
        playerExecutables.put(player.getName(), player);
        playerRepository.save(player);
    }
}
