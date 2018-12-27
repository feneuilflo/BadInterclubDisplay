package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.Player;
import com.bad.interclub.display.bach.model.ScoreUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MatchScoreController implements Initializable {

    @FXML
    private Region rgnPlayers;

    @FXML
    private Label lblHostPlayer;

    @FXML
    private Label lblGuestPlayer;

    @FXML
    private Region rgnSet1;

    @FXML
    private Label lblHostSet1;

    @FXML
    private Label lblGuestSet1;

    @FXML
    private Region rgnSet2;

    @FXML
    private Label lblHostSet2;

    @FXML
    private Label lblGuestSet2;

    @FXML
    private Region rgnSet3;

    @FXML
    private Label lblHostSet3;

    @FXML
    private Label lblGuestSet3;

    private IntegerProperty winnerSet1 = new SimpleIntegerProperty();
    private IntegerProperty winnerSet2 = new SimpleIntegerProperty();
    private IntegerProperty winnerSet3 = new SimpleIntegerProperty();
    private IntegerProperty score = new SimpleIntegerProperty();

    private ChangeListener<Number> listenerHostSet1;
    private ChangeListener<Number> listenerHostSet2;
    private ChangeListener<Number> listenerHostSet3;
    private ChangeListener<Number> listenerGuestSet1;
    private ChangeListener<Number> listenerGuestSet2;
    private ChangeListener<Number> listenerGuestSet3;

    private Match match;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setup listeners
        listenerHostSet1 = (observable, oldValue, newValue) -> lblHostSet1.setText(scoreToString(newValue.intValue()));
        listenerHostSet2 = (observable, oldValue, newValue) -> lblHostSet2.setText(scoreToString(newValue.intValue()));
        listenerHostSet3 = (observable, oldValue, newValue) -> lblHostSet3.setText(scoreToString(newValue.intValue()));
        listenerGuestSet1 = (observable, oldValue, newValue) -> lblGuestSet1.setText(scoreToString(newValue.intValue()));
        listenerGuestSet2 = (observable, oldValue, newValue) -> lblGuestSet2.setText(scoreToString(newValue.intValue()));
        listenerGuestSet3 = (observable, oldValue, newValue) -> lblGuestSet3.setText(scoreToString(newValue.intValue()));

        // players background
        rgnPlayers.setStyle("-fx-background-color: linear-gradient(to bottom, -host-color-1 0%, -host-color-2 47%, #600060 50%, -guest-color-2 53%, -guest-color-1 100%)");
    }

    public void setMatch(Match match) {
        if(this.match != null) {
            // remove listeners
            this.match.getScore().getSet1().hostPointsProperty().removeListener(listenerHostSet1);
            this.match.getScore().getSet1().guestPointsProperty().removeListener(listenerGuestSet1);
            this.match.getScore().getSet2().hostPointsProperty().removeListener(listenerHostSet2);
            this.match.getScore().getSet2().guestPointsProperty().removeListener(listenerGuestSet2);
            this.match.getScore().getSet3().hostPointsProperty().removeListener(listenerHostSet3);
            this.match.getScore().getSet3().guestPointsProperty().removeListener(listenerGuestSet3);

            rgnPlayers.styleProperty().unbind();
            rgnSet1.styleProperty().unbind();
            rgnSet2.styleProperty().unbind();
            rgnSet3.styleProperty().unbind();
        }

        // setup background
        rgnSet1.styleProperty().bind(fromScoreToBackground(ScoreUtils.getWinnerProperty(match.getScore().getSet1())));
        rgnSet2.styleProperty().bind(fromScoreToBackground(ScoreUtils.getWinnerProperty(match.getScore().getSet2())));
        rgnSet3.styleProperty().bind(fromScoreToBackground(ScoreUtils.getWinnerProperty(match.getScore().getSet3())));

        // add listeners for score
        this. match = match;
        this.match.getScore().getSet1().hostPointsProperty().addListener(listenerHostSet1);
        this.match.getScore().getSet1().guestPointsProperty().addListener(listenerGuestSet1);
        this.match.getScore().getSet2().hostPointsProperty().addListener(listenerHostSet2);
        this.match.getScore().getSet2().guestPointsProperty().addListener(listenerGuestSet2);
        this.match.getScore().getSet3().hostPointsProperty().addListener(listenerHostSet3);
        this.match.getScore().getSet3().guestPointsProperty().addListener(listenerGuestSet3);

        // fill current score
        lblHostSet1.setText(scoreToString(this.match.getScore().getSet1().getHostPoints()));
        lblHostSet2.setText(scoreToString(this.match.getScore().getSet2().getHostPoints()));
        lblHostSet3.setText(scoreToString(this.match.getScore().getSet3().getHostPoints()));
        lblGuestSet1.setText(scoreToString(this.match.getScore().getSet1().getGuestPoints()));
        lblGuestSet2.setText(scoreToString(this.match.getScore().getSet2().getGuestPoints()));
        lblGuestSet3.setText(scoreToString(this.match.getScore().getSet3().getGuestPoints()));

        // fill players name
        lblHostPlayer.setWrapText(match.getHost().size() == 1); // wrap text for single matches only
        lblGuestPlayer.setWrapText(match.getGuest().size() == 1);
        lblHostPlayer.setText(match.getHost().stream().map(pl -> pl.getSurname().toUpperCase() + " " + pl.getFirstname()).collect(Collectors.joining("\n")));
        lblGuestPlayer.setText(match.getGuest().stream().map(pl -> pl.getSurname().toUpperCase() + " " + pl.getFirstname()).collect(Collectors.joining("\n")));
    }

    private static String scoreToString(int score) {
        return score > 0 ? String.valueOf(score) : "-";
    }

    private StringBinding fromScoreToBackground(ObservableIntegerValue score) {
        return Bindings.createStringBinding(() -> getScoreBackgroundColor(score.get()), score);
    }

    private String getScoreBackgroundColor(int score) {
        return String.format("-fx-background-color: linear-gradient(to bottom, -host-color-1 0%%, -host-color-2 %d%%, #600060 %d%%, -guest-color-2 %d%%, -guest-color-1 100%%);",
                47 + score * 12, 50 + score * 12, 53 + score * 12);
    }
}
