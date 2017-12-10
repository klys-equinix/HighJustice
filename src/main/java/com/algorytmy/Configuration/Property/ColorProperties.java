package com.algorytmy.Configuration.Property;

import com.algorytmy.Model.Match;
import javafx.scene.paint.Paint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="highjustice.gui.color")
public class ColorProperties {
    private String player1;
    private String player2;
    private String background;
    private String obstacle;

    public Paint getEntityColor(Match.FIELD_VALUE pc)
    {
        if(pc == Match.FIELD_VALUE.EMPTY)
            return Paint.valueOf("black");
        else if(pc == Match.FIELD_VALUE.P1)
            return Paint.valueOf("red");
        else if(pc == Match.FIELD_VALUE.P2)
            return Paint.valueOf("blue");
        else
            return Paint.valueOf("white");
    }
}