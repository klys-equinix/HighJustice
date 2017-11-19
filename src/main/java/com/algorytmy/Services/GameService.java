package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionExcpetion;
import com.algorytmy.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    MatchResultRepository matchResultRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    Map<String, Player> executablePlayers;

    private Match currentMatch;
    private Player currentPlayer;
    private Player otherPlayer;
    private boolean isFirstMove = true;
    private Move lastValidMove;

    /**
     * Create game for GUI mode - you are expected to call nextMove to progress the game.
     *
     * @return new Match with created Board
     * @throws IOException if one of the players cannot be executed
     */
    public Match createGame(Match possibleMatch) throws ExecutionExcpetion {
        this.currentMatch = possibleMatch;
        currentPlayer = executablePlayers.get(possibleMatch.getPlayer1().getName());
        otherPlayer = executablePlayers.get(possibleMatch.getPlayer2().getName());
        try {
            initiateProcess(currentPlayer.getPlayerExecutable());
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            currentMatch.setMatchResult(matchResult);
            throw new ExecutionExcpetion("Cannot run player executable", currentPlayer);
        }
        try {
            initiateProcess(otherPlayer.getPlayerExecutable());
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            currentMatch.setMatchResult(matchResult);
            throw new ExecutionExcpetion("Cannot run player executable", otherPlayer);
        }
        Integer boardSize = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(100, 999)).filter(n -> n % 2 == 1).limit(1).boxed().toArray(Integer[]::new)[0];
        possibleMatch.setBoard(new Match.FIELD_VALUE[boardSize][boardSize]);
        try {
            if (!writeAndRead(boardSize.toString(), currentPlayer.getPlayerExecutable().getWriter(), currentPlayer.getPlayerExecutable().getReader()).equals("OK"))
                throw new IOException();
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.TIMEOUT);
            currentMatch.setMatchResult(matchResult);
            throw new ExecutionExcpetion("Player1 not responding", currentPlayer);
        }
        try {
            if (!writeAndRead(boardSize.toString(), otherPlayer.getPlayerExecutable().getWriter(), otherPlayer.getPlayerExecutable().getReader()).equals("OK"))
                throw new IOException();
        } catch (IOException e) {
            MatchResult matchResult = new MatchResult(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.TIMEOUT);
            currentMatch.setMatchResult(matchResult);
            throw new ExecutionExcpetion("Player2 not responding", otherPlayer);
        }
        currentPlayer.setPlayerSignature(Match.FIELD_VALUE.P1);
        otherPlayer.setPlayerSignature(Match.FIELD_VALUE.P2);
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
     *
     * @return
     */
    public Move nextMove() {
        if (isFirstMove) {
            isFirstMove = false;
            try {
                Move firstMove = validateMove(new Move(writeAndRead("start", currentPlayer.getPlayerExecutable().getWriter(), currentPlayer.getPlayerExecutable().getReader()),
                        currentPlayer));
                if(firstMove != null) {
                    switchPlayers();
                    lastValidMove = firstMove;
                }
                return firstMove;
            } catch (IOException e) {
                logger.error(e.getMessage());
                MatchResult matchResult = new MatchResult(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.TIMEOUT);
                currentMatch.setMatchResult(matchResult);
                return null;
            }
        }
        try {
            Move move = validateMove(new Move(writeAndRead(lastValidMove.toString(), currentPlayer.getPlayerExecutable().getWriter(), currentPlayer.getPlayerExecutable().getReader()),
                    currentPlayer));
            if(move != null) {
                switchPlayers();
                lastValidMove = move;
            }
            return move;
        } catch (IOException e) {
            logger.error(e.getMessage());
            MatchResult matchResult = new MatchResult(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.TIMEOUT);
            currentMatch.setMatchResult(matchResult);
            return null;
        }
    }

    public void endMatch() {
        try {
            writeAndRead("stop", currentPlayer.getPlayerExecutable().getWriter(), currentPlayer.getPlayerExecutable().getReader());
            writeAndRead("stop", otherPlayer.getPlayerExecutable().getWriter(), otherPlayer.getPlayerExecutable().getReader());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        MatchResult matchResult = currentMatch.getMatchResult();
        matchResultRepository.save(matchResult);
        currentMatch = null;
        matchResult.getWinner().addToScore();
        playerRepository.save(matchResult.getWinner());
        playerRepository.save(matchResult.getLoser());
        closePlayerProcess(currentPlayer);
        closePlayerProcess(otherPlayer);
    }

    private Move validateMove(Move move) {
        if (move.getX1() < currentMatch.getBoard()[0].length && move.getX1() > 0 &&
                move.getY1() < currentMatch.getBoard().length && move.getY1() > 0 &&
                move.getX2() < currentMatch.getBoard()[0].length && move.getX2() > 0 &&
                move.getY2() < currentMatch.getBoard().length && move.getY2() > 0) {
            if(currentMatch.getBoard()[move.getX1()][move.getY1()].equals(Match.FIELD_VALUE.EMPTY) &&
                    currentMatch.getBoard()[move.getX2()][move.getY2()] == Match.FIELD_VALUE.EMPTY) {
                Match.FIELD_VALUE[][] board = currentMatch.getBoard();
                board[move.getX1()][move.getY1()] = currentPlayer.getPlayerSignature();
                board[move.getX2()][move.getY2()] = currentPlayer.getPlayerSignature();
                return move;
            }
        }
        MatchResult matchResult = new MatchResult(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.WRONG_INSERTION);
        currentMatch.setMatchResult(matchResult);
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
            logger.error(e.getMessage());
        }
        String line;
        while ((line = reader.readLine()) != null) ; // skip to last line, probably will have to change to WatchService
        return line;
    }

    private void closePlayerProcess(Player player) {
        try {
            player.getPlayerExecutable().getReader().close();
            player.getPlayerExecutable().getWriter().close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        player.getPlayerExecutable().getProcess().destroy();
    }

    private void switchPlayers() {
        Player tmpPlayer = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = tmpPlayer;
    }

    public Match getCurrentMatch() {
        return currentMatch;
    }

}
