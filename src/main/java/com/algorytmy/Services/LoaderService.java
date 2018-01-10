package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionException;
import com.algorytmy.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
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

    @Value("${game.singlegame: false}")
    private boolean singleGameMode;

    public void loadPlayers(File playersFolder) throws IOException {
        File[] directories = playersFolder.listFiles(file -> file.isDirectory());
        if(directories.length == 0) {
            throw new IOException("Folder empty");
        }
        for(File directory : directories) {
            parseDirectory(directory);
        }
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
                    match.setMatchStatus(MatchStatus.PENDING);
                    if(!singleGameMode) {
                        possibleMatches.add(match);
                    } else if (!possibleMatches.contains(match)){
                        possibleMatches.add(match);
                    }
                }
            });
        });

    }

    public Match.FIELD_VALUE[][] loadObstacles(File obstacleFile, Match.FIELD_VALUE[][] board) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(obstacleFile));
        String line = "";
        while((line = fileReader.readLine()) != null) {
            Move move = new Move(line, null);
            board[move.getX1()][move.getY1()] = Match.FIELD_VALUE.OBSTACLE;
            board[move.getX2()][move.getY2()] = Match.FIELD_VALUE.OBSTACLE;
        }
        return board;
    }

    private void parseDirectory(File directory) throws FileNotFoundException {
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
        if(command.isEmpty() || path.isEmpty()) {
            throw new FileNotFoundException("Incorrect folder");
        }
        playerExecutable.setCommandLineExecution(command + " " + path);
        player.setPlayerExecutable(playerExecutable);
        playerExecutables.put(player.getName(), player);
        playerRepository.save(player);
    }
}
