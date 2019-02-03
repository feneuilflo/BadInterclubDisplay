package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.ScoreUtils;
import com.bad.interclub.display.bach.ui.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SummaryController.class);


    @FXML
    private Region node;

    @FXML
    private ListView<Match.EMatchType> listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setCellFactory(listView -> createCell());

        listView.setItems(App.getModelInstance().getMatchOrder());
    }

    private ListCell<Match.EMatchType> createCell() {
        return new ListCell<Match.EMatchType>() {
            @Override
            protected void updateItem(Match.EMatchType item, boolean empty) {
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    // create node
                    setGraphic(createMatchNode(item));

                    // bind style to winner
                    Match match = App.getModelInstance().getMatches().get(item);
                    ScoreUtils.getWinnerProperty(match).addListener((observable, oldValue, newValue) -> setStyle(getStyle(newValue.intValue())));
                    setStyle(getStyle(ScoreUtils.getWinner(match)));
                }
            }

            private String getStyle(int winner) {
                if(winner > 0) {
                    return "-fx-padding: 0px; -fx-background-color: -host-color-2;";
                } else if (winner < 0) {
                    return "-fx-padding: 0px; -fx-background-color: -guest-color-2;";
                } else {
                    return "-fx-padding: 0px; ";
                }
            }
        };
    }

    private Node createMatchNode(Match.EMatchType matchType) {
        Match match = App.getModelInstance().getMatches().get(matchType);

        // Load person overview.
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
