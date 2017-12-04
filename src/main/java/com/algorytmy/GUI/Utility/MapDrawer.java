package com.algorytmy.GUI.Utility;

import com.algorytmy.Configuration.Property.ColorProperties;
import com.algorytmy.Model.Match;
import javafx.scene.canvas.GraphicsContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MapDrawer {
    @Autowired
    private ColorProperties colorProperties;

    public void drawBoard(GraphicsContext gc, Match.FIELD_VALUE[][] board) {
        /*
        * Assuming board is n by n
        */
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board.length; j++)
            {
                gc.setFill(colorProperties.getEntityColor(board[i][j]));
                gc.fillRect(i, j, 1, 1);
            }
        }
    }

    public Match.FIELD_VALUE[][] randomizeBoard()
    {
        Match.FIELD_VALUE[][] board = new Match.FIELD_VALUE[1000][1000];
        Random rand = new Random();
        for(int i = 0; i < 1000; i++)
            for(int j = 0; j < 1000; j++)
            {
                int r = rand.nextInt(4);
                if(r == 1)
                    board[i][j] = Match.FIELD_VALUE.EMPTY;
                else if (r == 2)
                    board[i][j] = Match.FIELD_VALUE.OBSTACLE;
                else if (r == 3)
                    board[i][j] = Match.FIELD_VALUE.P1;
                else
                    board[i][j] = Match.FIELD_VALUE.P2;

            }
        return board;
    }
}
