package com.algorytmy.GUI.Utility;

import com.algorytmy.Model.Match;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class MapDrawer {
    private static final Logger logger = LoggerFactory.getLogger(MapDrawer.class);

    public WritableImage drawBoard(Match.FIELD_VALUE[][] board, int zoom) {
        BufferedImage buff = new BufferedImage(board.length*2*zoom, board.length*2*zoom, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) buff.getGraphics();
        g.scale(zoom, zoom);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, board.length*2, board.length*2);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                g.setColor(getEntityColor(board[i][j]));
                g.fillRect(i*2, j*2, 1, 1);
            }
        }

        logger.info("Image of board has been generated.");
        return SwingFXUtils.toFXImage(buff, null);
    }

    private Color getEntityColor(Match.FIELD_VALUE fv) {
        if(fv == Match.FIELD_VALUE.EMPTY)
            return Color.BLACK;
        else if(fv == Match.FIELD_VALUE.OBSTACLE)
            return Color.WHITE;
        else if(fv == Match.FIELD_VALUE.P1)
            return Color.RED;
        else
            return Color.BLUE;
    }
}
