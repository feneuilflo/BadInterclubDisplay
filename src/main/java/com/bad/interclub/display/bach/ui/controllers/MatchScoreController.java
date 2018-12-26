package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MatchScoreController implements Initializable {


    @FXML
    private Label lblHostPlayer;

    @FXML
    private Label lblGuestPlayer;

    @FXML
    private Label lblHostSet1;

    @FXML
    private Label lblGuestSet1;

    @FXML
    private Label lblHostSet2;

    @FXML
    private Label lblGuestSet2;

    @FXML
    private Label lblHostSet3;

    @FXML
    private Label lblGuestSet3;

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
        listenerHostSet1 = (observable, oldValue, newValue) -> lblHostSet1.setText(String.valueOf(newValue));
        listenerHostSet2 = (observable, oldValue, newValue) -> lblHostSet2.setText(String.valueOf(newValue));
        listenerHostSet3 = (observable, oldValue, newValue) -> lblHostSet3.setText(String.valueOf(newValue));
        listenerGuestSet1 = (observable, oldValue, newValue) -> lblGuestSet1.setText(String.valueOf(newValue));
        listenerGuestSet2 = (observable, oldValue, newValue) -> lblGuestSet2.setText(String.valueOf(newValue));
        listenerGuestSet3 = (observable, oldValue, newValue) -> lblGuestSet3.setText(String.valueOf(newValue));
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
        }

        // add listeners
        this. match = match;
        this.match.getScore().getSet1().hostPointsProperty().addListener(listenerHostSet1);
        this.match.getScore().getSet1().guestPointsProperty().addListener(listenerGuestSet1);
        this.match.getScore().getSet2().hostPointsProperty().addListener(listenerHostSet2);
        this.match.getScore().getSet2().guestPointsProperty().addListener(listenerGuestSet2);
        this.match.getScore().getSet3().hostPointsProperty().addListener(listenerHostSet3);
        this.match.getScore().getSet3().guestPointsProperty().addListener(listenerGuestSet3);

        // fill players name
        lblHostPlayer.setText(match.getHost().stream().map(pl -> pl.getFirstname().toUpperCase() + " " + pl.getSurname()).collect(Collectors.joining("\n")));
        lblGuestPlayer.setText(match.getGuest().stream().map(pl -> pl.getFirstname().toUpperCase() + " " + pl.getSurname()).collect(Collectors.joining("\n")));
    }
}
