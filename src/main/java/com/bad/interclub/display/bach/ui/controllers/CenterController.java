package com.bad.interclub.display.bach.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
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

        System.err.println(court1);
        System.err.println(court2);
        System.err.println(court1Controller);
        System.err.println(court2Controller);


        court1Controller.setCourtName("Terrain 1");
        court2Controller.setCourtName("Terrain 2");

    }
}
