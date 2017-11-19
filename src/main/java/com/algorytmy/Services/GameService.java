package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionExcpetion;
import com.algorytmy.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 */
@Service
public class GameService {

    @Autowired
    MatchResultRepository matchResultRepository;

    @Autowired
    Map<String, PlayerExecutable> playerExecutables;

    private Match currentMatch;
    private PlayerExecutable playerExecutable1;
    private PlayerExecutable playerExecutable2;
    private boolean isFirstMove = true;
    private PlayerExecutable currentPlayer;

    /**
     * Create game for GUI mode - you are expected to call nextMove to progress the game.
     * @return new Match with created Board
     * @throws IOException if one of the players cannot be executed
     */
    public Match createGame(Match possibleMatch) throws ExecutionExcpetion {
        playerExecutable1 = playerExecutables.get(possibleMatch.getPlayer1().getName());
        playerExecutable2 = playerExecutables.get(possibleMatch.getPlayer2().getName());
        try {
            initiateProcess(playerExecutable1);
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            matchResultRepository.save(matchResult);
            throw new ExecutionExcpetion("Cannot run player executable", playerExecutable1);
        }
        try {
            initiateProcess(playerExecutable2);
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            matchResultRepository.save(matchResult);
            throw new ExecutionExcpetion("Cannot run player executable", playerExecutable2);
        }
        Integer boardSize = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(100, 999)).filter(n -> n % 2 == 1).limit(1).boxed().toArray(Integer[]::new)[0];
        possibleMatch.setBoard(new Integer[boardSize][boardSize]);
        try {
            if(!writeAndRead(boardSize.toString(), playerExecutable1.getWriter(), playerExecutable1.getReader()).equals("OK"))
                throw new IOException();
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.TIMEOUT);
            matchResultRepository.save(matchResult);
            throw new ExecutionExcpetion("Player1 not responding", playerExecutable1);
        }
        try {
            if(!writeAndRead(boardSize.toString(), playerExecutable2.getWriter(), playerExecutable2.getReader()).equals("OK"))
                throw new IOException();
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.TIMEOUT);
            matchResultRepository.save(matchResult);
            throw new ExecutionExcpetion("Player2 not responding", playerExecutable2);
        }
        this.currentPlayer = playerExecutable1;
        this.currentMatch = possibleMatch;
        return currentMatch;
    }

    /**
     * Call this for automatic mode - all games will be run concurrently, without communicating with GUI.
     * Only final statistics are to be displayed.
      */
    public void runAllGames() {

    }

    /**
     * If this returns null, it means the game has ended
     * @return
     */
    public Move nextMove() {
        if(isFirstMove) {
            try {
               writeAndRead("start", currentPlayer.getWriter(), currentPlayer.getReader());
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
    private void initiateProcess(PlayerExecutable playerExecutable) throws IOException {
        playerExecutable.setProcess(Runtime.getRuntime().exec(playerExecutable.getCommandLineExecution()));
        playerExecutable.setWriter(new BufferedWriter(new OutputStreamWriter(playerExecutable.getProcess().getOutputStream())));
        playerExecutable.setReader(new BufferedReader(new InputStreamReader(playerExecutable.getProcess().getInputStream())));
    }

    private String writeAndRead(String message, BufferedWriter writer, BufferedReader reader) throws IOException {
        Long time = System.nanoTime();
        writer.write(message);
        writer.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        String line;
        while ((line = reader.readLine()) != null); // skip to last line, probably will have to change to WatchService
        return line;
    }
    public Match getCurrentMatch() {
        return currentMatch;
    }
}
