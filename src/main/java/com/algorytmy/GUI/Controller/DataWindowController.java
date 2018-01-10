package com.algorytmy.GUI.Controller;

import com.algorytmy.Exceptions.ExecutionException;
import com.algorytmy.GUI.View.HistoryWindowView;
import com.algorytmy.GUI.View.MapWindowView;
import com.algorytmy.JudgeApplication;
import com.algorytmy.Model.*;
import com.algorytmy.Services.AutoGameRunner;
import com.algorytmy.Services.GameService;
import com.algorytmy.Services.LoaderService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

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


    @FXML
    private MenuItem exportResultsMenuItem;
    @FXML
    private TableView<Player> playersTable;
    @FXML
    private TableView<Match> matchTable;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem automaticSimulationMenuItem;
    @FXML
    private MenuItem openDirectoryMenuItem;
    @FXML
    private VBox container;
    @FXML
    private MenuItem boardSizeMenuItem;

    private ObservableList<Player> playerList;
    private ObservableList<Match> matchList;

    @Autowired
    private MapWindowController mwc;
    @Autowired
    private HistoryWindowController hwc;

    @Autowired
    private AutoGameRunner autoGameRunner;

    private int boardSize;
    private File obstacleFile;

    public DataWindowController() {
    }

    @FXML
    private void initialize() throws IOException {
        stg = JudgeApplication.getStage();
        playerList = FXCollections.observableArrayList();
        matchList = FXCollections.observableArrayList(person ->
                new Observable[]{person.getMatchStatusProperty()});

        playersTable.setItems(playerList);
        matchTable.setItems(matchList);

        TableColumn<Player, String> playerNameColumn = (TableColumn<Player, String>) playersTable.getColumns().get(0);
        TableColumn<Player, Integer> playerPointsColumn = (TableColumn<Player, Integer>) playersTable.getColumns().get(1);

        playerNameColumn.setStyle("-fx-alignment: CENTER;");
        playerPointsColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Match, String> player1Column = (TableColumn<Match, String>) matchTable.getColumns().get(0);
        TableColumn<Match, String> player2Column = (TableColumn<Match, String>) matchTable.getColumns().get(1);
        TableColumn<Match, MatchStatus> statusColumn = (TableColumn<Match, MatchStatus>) matchTable.getColumns().get(2);
        TableColumn<Match, String> resultColumn = (TableColumn<Match, String>) matchTable.getColumns().get(3);

        player1Column.setStyle("-fx-alignment: CENTER;");
        player2Column.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");
        resultColumn.setStyle("-fx-alignment: CENTER;");

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

        Platform.runLater(() -> {
            ((Stage) container.getScene().getWindow()).setTitle("HighJustice");
            container.getScene().getWindow().setOnHidden(windowEvent -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @FXML
    private void onOpenDirectorySelected(ActionEvent actionEvent) throws IOException {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose directory");
        File dir = dirChooser.showDialog(stg);
        if (dir == null)
            return;

        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("HighJustice - size of the map");
        dialog.setHeaderText("It's suggested to choose map size lower than 1000 units!");
        dialog.setContentText("Please enter desired map size:");

        Optional<String> result = dialog.showAndWait();
        Integer size;
        if (result.isPresent()){
            try {
                size = Integer.valueOf(result.get());
            } catch (NumberFormatException e) {
                showWarningDialog("Wrong map size entered!", "Try again!");
                return;
            }
        } else
            return;

        if (size <= 3 || size > 999 || size % 2 == 0) {
            showWarningDialog("Wrong map size entered!", "Try again!");
            return;
        }

        boardSize = size;
        boardSizeMenuItem.setText("Board size: "+size+" units");

        dirChooser.setTitle("Choose obstacle file or cancel");
        obstacleFile = dirChooser.showDialog(stg);

        try {
            loaderService.loadPlayers(dir);
        } catch (Exception e) {
            showWarningDialog("Cannot load players!", "The entered directory seems to be invalid.\nPlease try again!");
            return;
        }

        automaticSimulationMenuItem.setDisable(false);
        openDirectoryMenuItem.setDisable(true);

        playerList.addAll(playerRepository.findAll());
        matchList.addAll(possibleMatches);

        for (Match m : matchList) {
            m.addMathEndListener(mth -> {
                boolean found = false;
                for (int i = 0; i < matchList.size(); i++) {
                    Match min = matchList.get(i);
                    if (min.getPlayer1() == mth.getPlayer1() && min.getPlayer2() == mth.getPlayer2()) {
                        matchList.set(i, mth);
                    }
                    if (min.getMatchStatus() != MatchStatus.ENDED)
                        found = true;
                }

                if (!found)
                    exportResultsMenuItem.setDisable(false);

                playerList.clear();
                playerList.addAll(playerRepository.findAll());
            });
        }
    }

    @FXML
    private void onExportResultsSelected(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File dir = fileChooser.showSaveDialog(stg);
        if (dir == null)
            return;

        BufferedWriter bw;
        StringBuilder sb = new StringBuilder();
        try {
            bw = new BufferedWriter(new FileWriter(dir));
            sb.append("Tournament at ").append(Calendar.getInstance().getTime())
                    .append("\n\n").append("Players : Points\n\n");
            bw.write(sb.toString());
            sb.setLength(0);
            for (Player p : playerList) {
                sb.append(p.getName()).append(" : ").append(p.getScore()).append("\n");
            }
            bw.write(sb.toString());
            bw.write("\nMatches\n\n");
            sb.setLength(0);
            for (Match m : matchList) {
                sb.append(m.getPlayer1().getName()).append(" vs ").append(m.getPlayer2().getName()).append(" winner: ")
                        .append(m.getMatchResult().getWinner().getName()).append(", reason: ")
                        .append(m.getMatchResult().getGameEnder()).append("\n");
            }
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            showWarningDialog("There was an error when trying to write to file!",
                    "Results could be written to file.\nPlease try again later!");
        }
    }

    @FXML
    private void onAutomaticSimulationSelected(ActionEvent actionEvent) {
        autoGameRunner.runAllGames(boardSize, obstacleFile);
    }

    @FXML
    private void onManualSelected(ActionEvent actionEvent) {
        Match mtch = matchTable.getSelectionModel().getSelectedItem();
        if (mtch.getMatchStatus() == MatchStatus.ENDED)
            return;

        for (Match m : matchList) {
            if (m.getMatchStatus() == MatchStatus.IN_PROGRESS && m != mtch)
                return;
        }

        int i;
        for (i = 0; i < matchList.size(); i++) {
            Match n = matchList.get(i);
            if (n.getPlayer1() == mtch.getPlayer1() && n.getPlayer2() == mtch.getPlayer2()) {
                mtch = n;
                break;
            }
        }
        try {
            gameService.createGame(mtch, boardSize, obstacleFile);
            mtch.setMatchStatus(MatchStatus.IN_PROGRESS);
            matchList.set(i, mtch);
        } catch (ExecutionException | IOException executionExcepetion) {
            showErrorDialog("There was a problem when trying to run program!", executionExcepetion.toString());
        }

        contextMenu.hide();

        mwc.onOpen();
        JudgeApplication.showView(MapWindowView.class, Modality.APPLICATION_MODAL);

        automaticSimulationMenuItem.setDisable(true);
    }

    @FXML
    private void onAboutSelected(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("High Justice");
        alert.setContentText("Konrad Łyś - judge logic\nSzymon Chmal - GUI\nPowered by Spring and JavaFX!");
        alert.show();
    }

    @FXML
    private void onHistorySelected(ActionEvent actionEvent) {
        contextMenu.hide();
        Match mtch = matchTable.getSelectionModel().getSelectedItem();
        if (mtch.getMatchStatus() != MatchStatus.ENDED)
            return;

        hwc.setMatch(mtch);
        JudgeApplication.showView(HistoryWindowView.class, Modality.APPLICATION_MODAL);
    }

    private void showErrorDialog(String header, String content) {
        showErrorDialog(header, content);
        stg.close();
    }


    private void showWarningDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error occured");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static String getHumanMatchResult(MatchResult mr) {
        if(mr.getGameEnder() == null)
            return "";

        StringBuilder sb = new StringBuilder();
        if (mr.getGameEnder() != MatchResult.GAME_ENDER.DEFAULT) {
            sb.append(mr.getGameEnder()).append(" : ").append(mr.getLoser().getName());
        } else {
            sb.append(mr.getWinner().getName()).append(" won");
        }
        return sb.toString();
    }
}
