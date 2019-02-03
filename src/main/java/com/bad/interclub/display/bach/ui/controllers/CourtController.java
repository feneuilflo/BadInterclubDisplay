package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.ui.App;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CourtController implements Initializable {

    @FXML
    private Label lblCourtName;

    @FXML
    private Label lblMatchName;

    @FXML
    private ImageView imgView;

    @FXML
    private MatchScoreController scoreController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // load image
        Image img = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("Court.png")).toExternalForm());
        imgView.setImage(img);
    }

    public void setIndex(int idx) {
        // setup label
        lblCourtName.setText(String.format("Terrain %d", idx));

        // setup match
        ObservableMap<Integer, Match.EMatchType> currentMatches = App.getModelInstance().getCurrentMatches();
        currentMatches.addListener((MapChangeListener<? super Integer, ? super Match.EMatchType>) c -> {
            if(c.getKey() == idx && c.wasAdded()) {
                setMatch(App.getModelInstance().getMatches().get(c.getValueAdded()));
            }
        });
        if(currentMatches.containsKey(idx)) {
            setMatch(App.getModelInstance().getMatches().get(currentMatches.get(idx)));
        }
    }

    public void setMatch(Match match) {
        lblMatchName.setText(match.getType().toString());
        scoreController.setMatch(match);
    }
}
