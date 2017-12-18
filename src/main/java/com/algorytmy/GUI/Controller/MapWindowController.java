package com.algorytmy.GUI.Controller;

import com.algorytmy.GUI.Utility.MapDrawer;
import com.algorytmy.Model.Match;
import com.algorytmy.Model.Move;
import com.algorytmy.Services.GameService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

@FXMLController
@NoArgsConstructor
public class MapWindowController {
    @FXML
    private ImageView canvas;
    @FXML
    private Button nextStepButton;
    @FXML
    private Button twoStepButton;
    @FXML
    private Button endButton;
    @FXML
    private VBox container;
    @FXML
    private Button zoomInButton;
    @FXML
    private Button zoomOutButton;

    private IntegerProperty zoom = new SimpleIntegerProperty(1);

    @Autowired
    private GameService gs;

    @Autowired
    private MapDrawer md;

    private boolean initialized = false;
    private static final int MAX_ZOOM = 3;

    @FXML
    private void initialize() {
        redrawMap(zoom.getValue());

        nextStepButton.setOnAction(actionEvent -> {
            gs.nextMove();
            if(gs.getCurrentMatch() != null)
                redrawMap(zoom.getValue());
        });

        twoStepButton.setOnAction(actionEvent -> {
            for (int i = 0; i < 5; i++)
                gs.nextMove();
            if(gs.getCurrentMatch() != null)
                redrawMap(zoom.getValue());
        });

        endButton.setOnAction(actionEvent -> {
            while (gs.nextMove() != null) { }
            if(gs.getCurrentMatch() != null)
                redrawMap(zoom.getValue());
        });

        gs.getCurrentMatch().addMathEndListener(mtch -> {
            handleMatchEnding();
        });

        zoom.addListener((observable, oldValue, newValue) -> redrawMap(newValue.intValue()));

        zoomInButton.setDisable(true);

        initialized = true;
    }

    void onOpen() {
        gs.getCurrentMatch().addMathEndListener(mtch -> {
            handleMatchEnding();
            redrawMap(zoom.getValue());
        });

        Platform.runLater(() -> {
            nextStepButton.setDisable(false);
            twoStepButton.setDisable(false);
            endButton.setDisable(false);
            ((Stage) container.getScene().getWindow()).setTitle("HighJustice - map drawer");

            redrawMap(zoom.getValue());
        });
    }

    void redrawMap(int zoom) {
        canvas.setSmooth(false);
        canvas.setImage(md.drawBoard(gs.getCurrentMatch().getBoard(), zoom));
    }

    boolean isInitialized() {
        return initialized;
    }

    private void handleMatchEnding() {
        nextStepButton.setDisable(true);
        twoStepButton.setDisable(true);
        endButton.setDisable(true);
    }

    @FXML
    private void zoomInAction(ActionEvent ae) {
        zoom.setValue(zoom.getValue()+1);
        if(zoom.getValue() == MAX_ZOOM)
            zoomInButton.setDisable(true);
        else
            zoomOutButton.setDisable(false);
    }

    @FXML
    private void zoomOutAction(ActionEvent ae) {
        zoom.setValue(zoom.getValue()-1);
        if(zoom.getValue() == 1)
            zoomOutButton.setDisable(true);
        else
            zoomInButton.setDisable(false);
    }
}
