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
public class HistoryWindowController {
    @FXML
    private ImageView canvas;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
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
    private MapDrawer md;

    private boolean initialized = false;
    private static final int MAX_ZOOM = 10;

    private Match match;

    private int moveIterator;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            ((Stage) container.getScene().getWindow()).setTitle("HighJustice - match history");
        });

        zoom.addListener((observable, oldValue, newValue) -> canvas.setImage(md.drawBoard(match.getBoard(), zoom.getValue())));

        previousButton.setDisable(true);
        zoomOutButton.setDisable(true);

        initialized = true;
    }

    boolean isInitialized() {
        return initialized;
    }

    @FXML
    private void showNextMove(ActionEvent ae) {
        moveIterator++;
        Move m = match.getMatchResult().getMoveList().get(moveIterator);

        // Adding move so nothing to be removed
        match.getBoard()[m.getX1()][m.getY1()] =
                m.getPlayer() == match.getPlayer1() ? Match.FIELD_VALUE.P1 : Match.FIELD_VALUE.P2;

        match.getBoard()[m.getX2()][m.getY2()] =
                m.getPlayer() == match.getPlayer1() ? Match.FIELD_VALUE.P1 : Match.FIELD_VALUE.P2;

        if(moveIterator == getMovesCount()-1)
            nextButton.setDisable(true);
        else
            nextButton.setDisable(false);

        if(moveIterator == 0)
            previousButton.setDisable(true);
        else
            previousButton.setDisable(false);

        statusLabel.setText(getStatusString());
        canvas.setImage(md.drawBoard(match.getBoard(), zoom.getValue()));
        moveLabel.setText(getMoveString(m));
    }


    @FXML
    private void showPreviousMove(ActionEvent ae) {
        // Oops. We need to remove one move!
        Move movePrev = match.getMatchResult().getMoveList().get(moveIterator);
        match.getBoard()[movePrev.getX1()][movePrev.getY1()] = Match.FIELD_VALUE.EMPTY;
        match.getBoard()[movePrev.getX2()][movePrev.getY2()] = Match.FIELD_VALUE.EMPTY;

        // Done!
        moveIterator--;

        if(moveIterator == 0)
            previousButton.setDisable(true);
        else
            previousButton.setDisable(false);

        if(moveIterator == getMovesCount()-1)
            nextButton.setDisable(true);
        else
            nextButton.setDisable(false);

        statusLabel.setText(getStatusString());
        canvas.setImage(md.drawBoard(match.getBoard(), zoom.getValue()));
        moveLabel.setText(getMoveString(match.getMatchResult().getMoveList().get(moveIterator)));
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

    public void setMatch(Match mtch) {
        this.match = mtch;

        for(int i = 0; i < mtch.getBoard().length; i++) {
            for (int j = 0; j < mtch.getBoard().length; j++) {
                if(mtch.getBoard()[i][j] != Match.FIELD_VALUE.OBSTACLE)
                    mtch.getBoard()[i][j] = Match.FIELD_VALUE.EMPTY;
            }
        }

        Platform.runLater(() -> {
            previousButton.setDisable(true);
            zoomOutButton.setDisable(true);

            moveIterator = -1;
            showNextMove(null);
        });
    }

    private int getMovesCount() {
        return match.getMatchResult().getMoveList().size();
    }

    private int getMoveNumber() {
        return moveIterator+1;
    }

    private String getStatusString() {
        StringBuilder sb = new StringBuilder("Match: ")
                .append(match.getPlayer1().getName()).append(" : ").append(match.getPlayer2().getName())
                .append(" (Move ").append(getMoveNumber()).append(" of ").append(getMovesCount()).append(")");
        return sb.toString();
    }

    private String getMoveString(Move m) {
        StringBuilder sb = new StringBuilder("Move of ").append(m.getPlayer().getName())
                .append(" (").append(m.getX1()).append(", ").append(m.getY1()).append(") and (")
                .append(m.getX2()).append(", ").append(m.getY2()).append(")");
        return sb.toString();
    }
}
