package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
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

    public void setCourtName(String name) {
        lblCourtName.setText(name);
    }

    public void setMatch(Match match) {
        lblMatchName.setText(match.getType().toString());
        scoreController.setMatch(match);
    }
}
