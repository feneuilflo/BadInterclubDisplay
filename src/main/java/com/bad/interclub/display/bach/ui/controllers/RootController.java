package com.bad.interclub.display.bach.ui.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @FXML
    private Node node;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // compute default text font size based on scene size
        node.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                DoubleProperty fontSize = new SimpleDoubleProperty(0);
                fontSize.addListener((observable, oldValue, newValue) -> LOGGER.info("Setting default font size to {}%", newValue));
                fontSize.bind(newScene.widthProperty().add(newScene.heightProperty())
                        .divide(1280 + 720)
                        .multiply(100));
                node.styleProperty().bind(
                        Bindings.concat("-fx-font-size: ", fontSize.asString("%.0f")).concat("%;"));

            }
        });
    }
}
