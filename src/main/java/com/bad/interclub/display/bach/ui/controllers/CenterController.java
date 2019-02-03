package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import sun.plugin.services.PlatformService;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class CenterController implements Initializable {

    @FXML
    private Node node;

    @FXML
    private Node court1;

    @FXML
    private Node court2;

    @FXML
    private CourtController court1Controller;

    @FXML
    private CourtController court2Controller;

    private final IntegerProperty court1MatchIdx = new SimpleIntegerProperty(0);
    private final IntegerProperty court2MatchIdx = new SimpleIntegerProperty(1);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set court indexes
        court1Controller.setIndex(1);
        court2Controller.setIndex(2);

        // get model
        Interclub interclub = App.getModelInstance();
        ObservableMap<Integer, Match.EMatchType> currentMatches = interclub.getCurrentMatches();

        // load first matches when matches order changes
        interclub.getMatchOrder().addListener((ListChangeListener.Change<? extends Match.EMatchType> c) -> {
            court1MatchIdx.set(0);
            court2MatchIdx.set(1);
        });

        // load match on property changes
        court1MatchIdx.addListener((observable, oldValue, newValue) -> {
            int matchIdx = newValue.intValue() % interclub.getMatches().size();
            currentMatches.put(1, interclub.getMatchOrder().get(matchIdx));
        });
        court2MatchIdx.addListener((observable, oldValue, newValue) -> {
            int matchIdx = newValue.intValue() % interclub.getMatches().size();
            currentMatches.put(2, interclub.getMatchOrder().get(matchIdx));
        });

        // load initial matches
        if(!interclub.getMatchOrder().isEmpty()) {
            currentMatches.put(1, interclub.getMatchOrder().get(court1MatchIdx.get()));
            currentMatches.put(2, interclub.getMatchOrder().get(court2MatchIdx.get()));
        }

        // listen for key shorcut
        node.setOnKeyPressed(ev -> {
            if (ev.isControlDown() && ev.getCode() == KeyCode.DIGIT1) {
                // update court 1
                int current = court1MatchIdx.get();

                if (ev.isShiftDown()) {
                    // backward
                    int newValue = current - 1;
                    if (newValue < 0) newValue += interclub.getMatches().size();
                    court1MatchIdx.set(newValue);
                } else {
                    // forward
                    int newValue = (current + 1) % interclub.getMatches().size();
                    court1MatchIdx.set(newValue);
                }
            } else if (ev.isControlDown() && ev.getCode() == KeyCode.DIGIT2) {
                // update court 2
                int current = court2MatchIdx.get();

                if (ev.isShiftDown()) {
                    // backward
                    int newValue = current - 1;
                    if (newValue < 0) newValue += interclub.getMatches().size();
                    court2MatchIdx.set(newValue);
                } else {
                    // forward
                    int newValue = (current + 1) % interclub.getMatches().size();
                    court2MatchIdx.set(newValue);
                }
            }
        });

        node.focusedProperty().addListener((observable, oldValue, newValue) -> System.err.println("focused: " + newValue));
        Platform.runLater(node::requestFocus);
    }
}
