package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableLongValue;
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

        ObservableLongValue hostScoreProperty = ScoreUtils.getHostScoreProperty(model);
        lblHostPoints.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(hostScoreProperty.get()), hostScoreProperty));

        ObservableLongValue guestScoreProperty = ScoreUtils.getGuestScoreProperty(model);
        lblGuestPoints.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(guestScoreProperty.get()), guestScoreProperty));
    }
}
