package com.algorytmy.GUI.Controller;

import com.algorytmy.JudgeApplication;
import com.algorytmy.Services.GameService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class ComputingWindowController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private VBox container;

    @Autowired
    private GameService gameService;

    public ComputingWindowController() {}

    @FXML
    private void initialize() {

    }
}