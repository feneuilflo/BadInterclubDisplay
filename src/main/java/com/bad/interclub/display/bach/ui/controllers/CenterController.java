package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class CenterController implements Initializable {

    @FXML
    private Node court1;

    @FXML
    private Node court2;

    @FXML
    private CourtController court1Controller;

    @FXML
    private CourtController court2Controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set court names
        court1Controller.setCourtName("Terrain 1");
        court2Controller.setCourtName("Terrain 2");

        // find current matches
        Interclub interclub = App.getModelInstance();

        if(interclub.getMatches().values().stream().allMatch(m -> ScoreUtils.getWinner(m) != 0)) {
            // already finished ?
            // fill with first matches
            if(interclub.getMatchOrder().size() > 2) {
                court1Controller.setMatch(interclub.getMatches().get(interclub.getMatchOrder().get(0)));
                court2Controller.setMatch(interclub.getMatches().get(interclub.getMatchOrder().get(1)));
            }
        }
    }
}
