package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SummaryController.class);


    @FXML
    private GridPane node;

    private Map<Match.EMatchType, Node> children = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(int idx = 0; idx < App.getModelInstance().getMatches().size(); idx++) {
            node.getRowConstraints().add(createRowConstraint());
        }

        // create children
        App.getModelInstance().getMatches().forEach((matchType, match) -> children.put(matchType, createMatchNode(match)));

        // listen for match order
        App.getModelInstance().getMatchOrder().addListener((InvalidationListener) c -> onNewMatchOrder());
        onNewMatchOrder();
    }

    private void onNewMatchOrder() {
        node.getChildren().clear();
        for(int idx = 0; idx < App.getModelInstance().getMatchOrder().size(); idx++) {
            Match.EMatchType type = App.getModelInstance().getMatchOrder().get(idx);
            Match match = App.getModelInstance().getMatches().get(type);
            node.getRowConstraints().get(idx).setPercentHeight(match.getHost().size() == 1 ? 9.5 : 15.5);
            node.add(children.get(type), 0, idx);
        }
    }

    private RowConstraints createRowConstraint() {
        RowConstraints result = new RowConstraints();
        result.setMinHeight(10.0);
        result.setPercentHeight(12.5);
        result.setVgrow(Priority.SOMETIMES);

        return result;
    }

    private Node createMatchNode(Match match) {
        // Load fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/MatchSummary.fxml")));
        try {
            Node result = loader.load();

            // set match in controller
            SummaryMatchController controller = loader.getController();
            controller.setMatch(match);

            return result;
        } catch (IOException e) {
            LOGGER.error("Failed to load MatchSummary.fxml: ", e);
            return new Label(match.toString());
        }
    }
}
