package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.MatchScore;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummaryMatchController implements Initializable {

    @FXML
    private Region rgn;

    @FXML
    private Label lblMatchType;

    @FXML
    private Label lblHostPlayers;

    @FXML
    private Label lblGuestPlayers;

    @FXML
    private Label lblScore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMatch(Match match) {
        // match type
        lblMatchType.setText(String.valueOf(match.getType()));

        // players
        lblHostPlayers.setWrapText(match.getHost().size() == 1); // wrap text for single matches only
        lblGuestPlayers.setWrapText(match.getGuest().size() == 1);
        lblHostPlayers.setText(match.getHost().stream().map(pl -> pl.getSurname().toUpperCase() + " " + pl.getFirstname()).collect(Collectors.joining("\n")));
        lblGuestPlayers.setText(match.getGuest().stream().map(pl -> pl.getSurname().toUpperCase() + " " + pl.getFirstname()).collect(Collectors.joining("\n")));

        // score
        match.getScore().getSet1().hostPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        match.getScore().getSet1().guestPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        match.getScore().getSet2().hostPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        match.getScore().getSet2().guestPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        match.getScore().getSet3().hostPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        match.getScore().getSet3().guestPointsProperty().addListener((observable, oldValue, newValue) -> onScoreUpdate(match.getScore()));
        onScoreUpdate(match.getScore());

        // background based on winner
        rgn.styleProperty().bind(fromScoreToBackground(ScoreUtils.getWinnerProperty(match)));

        // border based on current matches
        ObservableMap<Integer, Match.EMatchType> currentMatches = App.getModelInstance().getCurrentMatches();
        App.getModelInstance().getCurrentMatches().addListener((MapChangeListener<? super Integer, ? super Match.EMatchType>) change -> {
            if(change.getValueAdded() == match.getType()) {
                rgn.getStyleClass().add("summary-match-border");
            } else if(change.getValueRemoved() == match.getType()) {
                rgn.getStyleClass().remove("summary-match-border");
            }
        });
        if(currentMatches.values().contains(match.getType())) {
            rgn.getStyleClass().add("summary-match-border");
        }
    }

    private StringBinding fromScoreToBackground(ObservableIntegerValue score) {
        return Bindings.createStringBinding(() -> getStyle(score.get()), score);
    }

    private String getStyle(int winner) {
        if(winner > 0) {
            return "-fx-padding: 0px; -fx-background-color: -host-color-2;";
        } else if (winner < 0) {
            return "-fx-padding: 0px; -fx-background-color: -guest-color-2;";
        } else {
            return "-fx-padding: 0px; ";
        }
    }

    private void onScoreUpdate(MatchScore score) {
        lblScore.setText(Stream.of(score.getSet1(), score.getSet2(), score.getSet3())
                .filter(setScore -> setScore.getHostPoints() + setScore.getGuestPoints() > 0)
                .map(setScore -> String.format("%d / %d", setScore.getHostPoints(), setScore.getGuestPoints()))
                .collect(Collectors.joining("\n")));
    }
}
