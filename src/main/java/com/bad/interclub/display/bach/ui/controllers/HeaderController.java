package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    private Label lblHostName;

    @FXML
    private Label lblHostPoints;

    @FXML
    private Label lblGuestName;

    @FXML
    private Label lblGuestPoints;

    public HeaderController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Interclub model = App.getModelInstance();

        lblHostName.setText(model.getHost().getName());
        lblGuestName.setText(model.getGuest().getName());

        lblHostPoints.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(ScoreUtils.getHostScore(model)), ScoreUtils.getHostScoreProperty(model)));
        lblGuestPoints.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(ScoreUtils.getGuestScore(model)), ScoreUtils.getGuestScoreProperty(model)));
    }
}
