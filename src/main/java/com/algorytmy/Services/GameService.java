package com.algorytmy.Services;

import com.algorytmy.Exceptions.ExecutionException;
import com.algorytmy.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.*;
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
    LoaderService loaderService;

    @Value("${game.initialdelay}")
    Integer initialdelay;

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
    public Match createGame(Match possibleMatch, Integer boardSize, File obstacleFile) throws ExecutionException, IOException {
        this.currentMatch = possibleMatch;
        currentMatch.setMatchStatus(MatchStatus.IN_PROGRESS);
        currentPlayer = possibleMatch.getPlayer1();
        otherPlayer = possibleMatch.getPlayer2();
        try {
            initiateProcess(currentPlayer.getPlayerExecutable());
        } catch (IOException e) {
            finalizeMatch(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            throw new ExecutionException("Cannot run player executable", currentPlayer);
        }

        try {
            initiateProcess(otherPlayer.getPlayerExecutable());
        } catch (IOException e) {
            finalizeMatch(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.CANNOT_EXECUTE);
            throw new ExecutionException("Cannot run player executable", otherPlayer);
        }

        if(boardSize == null) {
            boardSize = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(100, 999)).filter(n -> n % 2 == 1).limit(1).boxed().toArray(Integer[]::new)[0];
        }

        if(obstacleFile == null) {
            possibleMatch.createBoard(boardSize);
        } else {
            possibleMatch.createEmptyBoard(boardSize);
            possibleMatch.setBoard(loaderService.loadObstacles(obstacleFile, possibleMatch.getBoard()));
        }

        StringBuilder builder = new StringBuilder();
        builder.append(boardSize.toString());

        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(possibleMatch.getBoard()[i][j].equals(Match.FIELD_VALUE.OBSTACLE)) {
                    builder.append("_" + i + "x" + j);
                }
            }
        }

        String initMessage = builder.toString();

        try {
            if (!writeAndRead(initMessage, currentPlayer).equalsIgnoreCase("OK"))
                throw new IOException();
        } catch (IOException e) {
            finalizeMatch(possibleMatch.getPlayer2(), possibleMatch.getPlayer1(), MatchResult.GAME_ENDER.TIMEOUT);
            throw new ExecutionException("Player1 not responding", currentPlayer);
        }
        try {
            if (!writeAndRead(initMessage, otherPlayer).equalsIgnoreCase("OK"))
                throw new IOException();
        } catch (IOException e) {
            finalizeMatch(possibleMatch.getPlayer1(), possibleMatch.getPlayer2(), MatchResult.GAME_ENDER.TIMEOUT);
            throw new ExecutionException("Player2 not responding", otherPlayer);
        }
        currentPlayer.setPlayerSignature(Match.FIELD_VALUE.P1);
        otherPlayer.setPlayerSignature(Match.FIELD_VALUE.P2);
        return currentMatch;
    }

    /**
     * If this returns null, it means the game has ended
     *
     * @return
     */
    public Move nextMove() {
        if (isFirstMove) {
            this.isFirstMove = false;
            try {
                Move firstMove = validateMove(new Move(writeAndRead("START", currentPlayer),
                        currentPlayer));
                if(firstMove != null) {
                    switchPlayers();
                    lastValidMove = firstMove;
                }
                return firstMove;
            } catch (IOException | ExecutionException e) {
                logger.error(e.getMessage());
                finalizeMatch(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.TIMEOUT);
                return null;
            }
        }
        if(noFreeSpaceLeft()) {
            finalizeMatch(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.DEFAULT);
            return null;
        }
        try {
            Move move = validateMove(new Move(writeAndRead(lastValidMove.toString(), currentPlayer),
                    currentPlayer));
            if(move != null) {
                switchPlayers();
                lastValidMove = move;
            }
            return move;
        } catch (IOException | ExecutionException e) {
            logger.error(e.getMessage());
            finalizeMatch(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.TIMEOUT);
            return null;
        }
    }

    @Transactional
    public void endMatch() {
        logger.debug("system" + " :" + currentPlayer.getName() + " stop");
        currentPlayer.getPlayerExecutable().getWriter().println("STOP");
        logger.debug("system" + " :" + otherPlayer.getName() + " stop");
        otherPlayer.getPlayerExecutable().getWriter().println("STOP");
        MatchResult matchResult = currentMatch.getMatchResult();
        matchResultRepository.save(matchResult);
        //currentMatch = null;
        matchResult.getWinner().addToScore();
        playerRepository.save(matchResult.getWinner());
        playerRepository.save(matchResult.getLoser());
        closePlayerProcess(currentPlayer);
        closePlayerProcess(otherPlayer);
    }

    private void finalizeMatch(Player winner, Player loser, MatchResult.GAME_ENDER gameEnder) {
        logger.debug("GAME_ENDER" + " :" + gameEnder + " loser: " + loser.getName() + " winner: " + winner.getName());
        isFirstMove = true;
        currentMatch.getMatchResult().setGameEnder(gameEnder);
        currentMatch.getMatchResult().setLoser(loser);
        currentMatch.getMatchResult().setWinner(winner);
        currentMatch.setMatchStatus(MatchStatus.ENDED);

        Match currentCopy = currentMatch;
        endMatch();
        currentCopy.getMatchEndListeners().forEach((matchEndListener -> matchEndListener.matchEnded(currentCopy)));
    }

    private boolean noFreeSpaceLeft() {
        for(int i = 0; i < currentMatch.getBoard().length; i++) {
            for(int j = 0; i < currentMatch.getBoard().length; i++) {
                if(i++ < currentMatch.getBoard().length && currentMatch.getBoard()[i][j].equals(Match.FIELD_VALUE.EMPTY) && currentMatch.getBoard()[i++][j].equals(Match.FIELD_VALUE.EMPTY)) {
                    return false;
                }
                if(j++ < currentMatch.getBoard().length && currentMatch.getBoard()[i][j].equals(Match.FIELD_VALUE.EMPTY) && currentMatch.getBoard()[i][j++].equals(Match.FIELD_VALUE.EMPTY)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Move validateMove(Move move) {
        currentMatch.getMatchResult().getMoveList().add(move);
        if (move.getX1() < currentMatch.getBoard()[0].length && move.getX1() >= 0 &&
                move.getY1() < currentMatch.getBoard().length && move.getY1() >= 0 &&
                move.getX2() < currentMatch.getBoard()[0].length && move.getX2() >= 0 &&
                move.getY2() < currentMatch.getBoard().length && move.getY2() >= 0) {
            if(currentMatch.getBoard()[move.getX1()][move.getY1()].equals(Match.FIELD_VALUE.EMPTY) &&
                    currentMatch.getBoard()[move.getX2()][move.getY2()].equals(Match.FIELD_VALUE.EMPTY)) {
                Match.FIELD_VALUE[][] board = currentMatch.getBoard();
                board[move.getX1()][move.getY1()] = currentPlayer.getPlayerSignature();
                board[move.getX2()][move.getY2()] = currentPlayer.getPlayerSignature();
                currentMatch.setBoard(board);
                logger.info(move.toString());
                return move;
            }
        }
        logger.debug("BAD_INSERTION " + move.toString() + " " + currentPlayer.getName());
        finalizeMatch(otherPlayer, currentPlayer, MatchResult.GAME_ENDER.WRONG_INSERTION);
        return null;
    }

    private void initiateProcess(PlayerExecutable playerExecutable) throws IOException {
        playerExecutable.setProcess(Runtime.getRuntime().exec(playerExecutable.getCommandLineExecution()));
        playerExecutable.setWriter(new PrintWriter(playerExecutable.getProcess().getOutputStream()));
    }

    private String writeAndRead(String message, Player player) throws IOException, ExecutionException {
        logger.debug("system" + " :" + message);
        player.getPlayerExecutable().getWriter().println(message);
        player.getPlayerExecutable().getWriter().flush();
        String line = "";
        Scanner scanner = new Scanner(player.getPlayerExecutable().getProcess().getInputStream());
        ExecutorService executor = Executors.newFixedThreadPool(1);
        int timeLimit = 500;
        if(isFirstMove) {
            timeLimit = initialdelay;
        }
        Future<String> future = executor.submit(() -> scanner.nextLine());
        try {
            line = future.get(timeLimit, TimeUnit.SECONDS);
        } catch (InterruptedException | java.util.concurrent.ExecutionException | TimeoutException e1) {
            throw new ExecutionException("Read timeout", player);
        }
        logger.debug(player.getName() + " :" + line);
        return line.replace("X", "x");
    }

    private void closePlayerProcess(Player player) {
        player.getPlayerExecutable().getWriter().close();
        player.getPlayerExecutable().getProcess().destroyForcibly();
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
