package com.algorytmy.GUI.Controller;

import com.algorytmy.GUI.Utility.MapDrawer;
import com.algorytmy.Services.GameService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class MapWindowController {
    @FXML
    private Canvas canvas;
    @FXML
    private Button nextStepButton;
    @FXML
    private Button twoStepButton;
    @FXML
    private Button endButton;
    @FXML
    private VBox container;

    private GraphicsContext gc;
    private Double zoom = 1.0;

    @Autowired
    private GameService gs;

    @Autowired
    private MapDrawer md;

    public MapWindowController() { }

    @FXML
    private void initialize() {
        gc = canvas.getGraphicsContext2D();
        canvas.setWidth(gs.getCurrentMatch().getBoard().length);
        canvas.setHeight(gs.getCurrentMatch().getBoard()[0].length);

        md.drawBoard(gc, gs.getCurrentMatch().getBoard());

        nextStepButton.setOnAction(actionEvent -> {
            gs.nextMove();
            md.drawBoard(gc, gs.getCurrentMatch().getBoard());
        });

        twoStepButton.setOnAction(actionEvent -> {
            for(int i = 0; i < 5; i++)
                gs.nextMove();
            md.drawBoard(gc, gs.getCurrentMatch().getBoard());
        });

        endButton.setOnAction(actionEvent -> {
            while (gs.nextMove() != null) { }
            md.drawBoard(gc, gs.getCurrentMatch().getBoard());
        });

        gs.addMathEndListener(mtch -> {
            nextStepButton.setDisable(true);
            twoStepButton.setDisable(true);
            endButton.setDisable(true);
        });
    }

    public void onOpen()
    {
        canvas.setWidth(gs.getCurrentMatch().getBoard().length);
        canvas.setHeight(gs.getCurrentMatch().getBoard()[0].length);
        md.drawBoard(gc, gs.getCurrentMatch().getBoard());
        nextStepButton.setDisable(false);
        twoStepButton.setDisable(false);
        endButton.setDisable(false);
    }

    @FXML
    private void onScroll(ScrollEvent scrollEvent)
    {
        if(zoom <= 1 && scrollEvent.getDeltaY() < 0)
            return;
        if(zoom >= 10 && scrollEvent.getDeltaY() > 0)
            return;

        zoom += scrollEvent.getDeltaY()/500;
        canvas.setScaleX(zoom);
        canvas.setScaleY(zoom);
    }

}
