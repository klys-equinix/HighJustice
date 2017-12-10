package com.algorytmy.GUI.Controller;

import com.algorytmy.Exceptions.ExecutionExcepetion;
import com.algorytmy.GUI.View.ComputingWindowView;
import com.algorytmy.GUI.View.MapWindowView;
import com.algorytmy.JudgeApplication;
import com.algorytmy.Model.*;
import com.algorytmy.Services.GameService;
import com.algorytmy.Services.LoaderService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

@FXMLController
public class DataWindowController {
    @Autowired
    private LoaderService loaderService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ArrayList<Match> possibleMatches;

    @Autowired
    private PlayerRepository playerRepository;

    private Stage stg;

    @Value(value = "classpath:Players")
    private Resource playersFolder;

    @FXML
    private MenuItem closeDirectoryMenuItem;
    @FXML
    private MenuItem exportResultsMenuItem;
    @FXML
    private TableView<Player> playersTable;
    @FXML
    private TableView<Match> matchTable;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem startAutomaticMenuItem;

    private ObservableList<Player> playerList;
    private ObservableList<Match> matchList;

    @Autowired
    private MapWindowController mwc;

    public DataWindowController() { }

    @FXML
    private void initialize() throws IOException {
        stg = JudgeApplication.getStage();
        playerList = FXCollections.observableArrayList();
        matchList = FXCollections.observableArrayList(person ->
                new Observable[] {person.getMatchStatusProperty()} );
        matchList.addListener((ListChangeListener.Change<? extends Match> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("Add");
                }
                if (change.wasUpdated()) {
                    System.out.println("Update");
                }
            }
        });

        playersTable.setItems(playerList);
        matchTable.setItems(matchList);

        TableColumn<Player, String> playerNameColumn = (TableColumn<Player, String>) playersTable.getColumns().get(0);
        TableColumn<Player, Integer> playerPointsColumn = (TableColumn<Player, Integer>) playersTable.getColumns().get(1);

        playerNameColumn.setStyle( "-fx-alignment: CENTER;");
        playerPointsColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<Match, String> player1Column = (TableColumn<Match, String>) matchTable.getColumns().get(0);
        TableColumn<Match, String> player2Column = (TableColumn<Match, String>) matchTable.getColumns().get(1);
        TableColumn<Match, MatchStatus> statusColumn = (TableColumn<Match, MatchStatus>) matchTable.getColumns().get(2);
        TableColumn<Match, String> resultColumn = (TableColumn<Match, String>) matchTable.getColumns().get(3);

        player1Column.setStyle( "-fx-alignment: CENTER;");
        player2Column.setStyle( "-fx-alignment: CENTER;");
        statusColumn.setStyle( "-fx-alignment: CENTER;");
        resultColumn.setStyle( "-fx-alignment: CENTER;");

        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        playerPointsColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        player1Column.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getPlayer1().getName());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        player2Column.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getPlayer2().getName());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("matchStatus"));
        resultColumn.setCellValueFactory(p -> {
            if (p.getValue() != null && p.getValue().getMatchResult() != null) {
                return new SimpleStringProperty(getHumanMatchResult(p.getValue().getMatchResult()));
            } else {
                return new SimpleStringProperty("-");
            }
        });

        gameService.addMathEndListener(mth -> {
            Match mtch = null;
            int i;
            for(i = 0; i < matchList.size(); i++) {
                Match n = matchList.get(i);
                if(n.getPlayer1() == mth.getPlayer1() && n.getPlayer2() == mth.getPlayer2())
                {
                    mtch = n;
                    break;
                }
            }
            mtch.setMatchStatus(MatchStatus.ENDED);
            mtch.setMatchResult(mth.getMatchResult());
            matchList.set(i, mtch);
        });
    }

    @FXML
    private void onOpenDirectorySelected(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose directory");
        File dir = dirChooser.showDialog(stg);
        if(dir == null)
            return;
        // Time to load but at the moment I will mock it up
        try {
            loaderService.loadPlayers(playersFolder.getFile());
        } catch (IOException e) {
            showErrorDialog("IO Exception", "Woops! Choose more wisely next time!");
            return;
        }
        closeDirectoryMenuItem.setDisable(false);
        exportResultsMenuItem.setDisable(false);
        startAutomaticMenuItem.setDisable(false);

        playerList.addAll(playerRepository.findAll());
        matchList.addAll(possibleMatches);
    }

    @FXML
    private void onCloseDirectorySelected(ActionEvent actionEvent) {
        // TODO
        closeDirectoryMenuItem.setDisable(true);
        exportResultsMenuItem.setDisable(true);
        startAutomaticMenuItem.setDisable(true);
    }

    @FXML
    private void onExportResultsSelected(ActionEvent actionEvent) {
        // TODO
    }

    @FXML
    private void onStartAutomaticSelected(ActionEvent actionEvent) {
        // TODO
        gameService.runAllGames();
        JudgeApplication.showView(ComputingWindowView.class, Modality.NONE);
    }

    @FXML
    private void onManualSelected(ActionEvent actionEvent) {
        // TODO
        Match mtch = matchTable.getSelectionModel().getSelectedItem();
        if(mtch.getMatchStatus() == MatchStatus.ENDED)
            return;

        for(Match m : matchList) {
            if(m.getMatchStatus() == MatchStatus.IN_PROGRESS && m != mtch)
                return;
        }

        int i;
        for(i = 0; i < matchList.size(); i++) {
            Match n = matchList.get(i);
            if(n.getPlayer1() == mtch.getPlayer1() && n.getPlayer2() == mtch.getPlayer2())
            {
                mtch = n;
                break;
            }
        }
        try {
            gameService.createGame(mtch);
            mtch.setMatchStatus(MatchStatus.IN_PROGRESS);
            matchList.set(i, mtch);
        } catch (ExecutionExcepetion executionExcepetion) {
            executionExcepetion.printStackTrace();
        }

        contextMenu.hide();
        JudgeApplication.showView(MapWindowView.class, Modality.WINDOW_MODAL);
        mwc.onOpen();
    }

    @FXML
    private void onAboutSelected(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("High Justice");
        alert.setContentText("Konrad Łyś - judge logic\nSzymon Chmal - GUI\nPowered by Spring and JavaFX!");
        alert.show();
    }

    private void showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error occured");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        stg.close();
    }

    private String getHumanMatchResult(MatchResult mr)
    {
        StringBuilder sb = new StringBuilder();
        if(mr.getGameEnder() != MatchResult.GAME_ENDER.DEFAULT) {
            sb.append(mr.getGameEnder()).append(" : ").append(mr.getLoser().getName());
        }
        else {
            sb.append(mr.getWinner().getName()).append(" won");
        }
        return sb.toString();
    }
}
