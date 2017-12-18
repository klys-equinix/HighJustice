package com.algorytmy.GUI.Controller;

import com.algorytmy.GUI.Utility.MapDrawer;
import com.algorytmy.Model.Match;
import com.algorytmy.Model.Move;
import com.algorytmy.Services.GameService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

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
    @FXML
    private Label statusLabel;
    @FXML
    private Label moveLabel;

    private IntegerProperty zoom = new SimpleIntegerProperty(1);

    @Autowired
    private GameService gs;

    @Autowired
    private MapDrawer md;

    private boolean initialized = false;
    private static final int MAX_ZOOM = 10;

    @FXML
    private void initialize() {
        redrawMap(zoom.getValue());

        nextStepButton.setOnAction(actionEvent -> {
            Move m = gs.nextMove();
            afterMove(m);
        });

        twoStepButton.setOnAction(actionEvent -> {
            Move m = null;
            for (int i = 0; i < 5; i++)
                m = gs.nextMove();
            afterMove(m);
        });

        endButton.setOnAction(actionEvent -> {
            Move m;
            while ((m = gs.nextMove()) != null) { }
            afterMove(m);
        });

        gs.getCurrentMatch().addMathEndListener(mtch -> {
            handleMatchEnding();
        });

        zoom.addListener((observable, oldValue, newValue) -> redrawMap(newValue.intValue()));

        zoomOutButton.setDisable(true);

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

            statusLabel.setText(getStatusString(gs.getCurrentMatch()));
            moveLabel.setText("Waiting for first move...");
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

    private String getStatusString(Match match) {
        StringBuilder sb = new StringBuilder("Match: ")
                .append(match.getPlayer1().getName()).append(" : ").append(match.getPlayer2().getName());
        return sb.toString();
    }

    private String getMoveString(Move m) {
        StringBuilder sb;
        if(m == null) {
            sb = new StringBuilder("Match ended with result: ")
                    .append(DataWindowController.getHumanMatchResult(gs.getCurrentMatch().getMatchResult()));
            return sb.toString();
        }
        sb = new StringBuilder("Move of ").append(m.getPlayer().getName())
                .append(" (").append(m.getX1()).append(", ").append(m.getY1()).append(") and (")
                .append(m.getX2()).append(", ").append(m.getY2()).append(")");
        return sb.toString();
    }

    private void afterMove(Move m) {
        if(gs.getCurrentMatch() != null) {
            redrawMap(zoom.getValue());
            statusLabel.setText(getStatusString(gs.getCurrentMatch()));
            moveLabel.setText(getMoveString(m));
        } else {
            moveLabel.setText("Match ended!");
        }
    }
}
