package com.bad.interclub.display.bach.ui.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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


        //
        try {
            Parent matchOrderRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/matchorder/Root.fxml")));

            Scene matchOrderScene = new Scene(matchOrderRoot, 600, 400);
            matchOrderScene.setFill(Color.TRANSPARENT);
            Stage matchOrderStage = new Stage();

            node.setOnKeyPressed(ev -> {
                if(ev.isControlDown() && ev.getCode() == KeyCode.N) {
                    matchOrderStage.show();
                }
            });

            Platform.runLater(() -> {
                matchOrderScene.getStylesheets().addAll(node.getScene().getStylesheets());

                matchOrderStage.initOwner(node.getScene().getWindow());
                matchOrderStage.initModality(Modality.APPLICATION_MODAL);
                matchOrderStage.initStyle(StageStyle.TRANSPARENT);
                matchOrderStage.centerOnScreen();
                matchOrderStage.setScene(matchOrderScene);

                matchOrderStage.show();
            });


        } catch (IOException ioe) {
            LOGGER.error("Failed to init match order Windows: ", ioe);
        }

    }
}
