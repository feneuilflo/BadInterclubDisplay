package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.Match;
import com.bad.interclub.display.bach.model.MatchOrderUtils;
import com.bad.interclub.display.bach.model.Player;
import com.bad.interclub.display.bach.ui.App;
import com.google.common.collect.ImmutableMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MatchOrderRootController implements Initializable {

    // see https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
    // https://stackoverflow.com/a/4382138
    private static final List<Color> colors = Arrays.asList(
            Color.web("0x817066"),    // Medium Gray
            Color.web("0xFFB300"),    // Vivid Yellow
            Color.web("0x803E75"),    // Strong Purple
            Color.web("0xFF6800"),    // Vivid Orange
            Color.web("0xA6BDD7"),    // Very Light Blue
            Color.web("0xC10020"),    // Vivid Red
            Color.web("0xCEA262"),    // Grayish Yellow

            Color.web("0x007D34"),    // Vivid Green
            Color.web("0xF6768E"),    // Strong Purplish Pink
            Color.web("0x00538A"),    // Strong Blue
            Color.web("0xFF7A5C"),    // Strong Yellowish Pink
            Color.web("0x53377A"),    // Strong Violet
            Color.web("0xFF8E00"),    // Vivid Orange Yellow
            Color.web("0xB32851"),    // Strong Purplish Red
            Color.web("0xF4C800"),    // Vivid Greenish Yellow
            Color.web("0x7F180D"),    // Strong Reddish Brown
            Color.web("0x93AA00"),    // Vivid Yellowish Green
            Color.web("0x593315"),    // Deep Yellowish Brown
            Color.web("0xF13A13"),    // Vivid Reddish Orange
            Color.web("0x232C16")     // Dark Olive Green
    );

    @FXML
    private Region rgnSH1;
    @FXML
    private Label lblOrderSH1;

    @FXML
    private Region rgnSH2;
    @FXML
    private Label lblOrderSH2;

    @FXML
    private Region rgnSD1;
    @FXML
    private Label lblOrderSD1;

    @FXML
    private Region rgnSD2;
    @FXML
    private Label lblOrderSD2;

    @FXML
    private Region rgnDD;
    @FXML
    private Label lblOrderDD;

    @FXML
    private Region rgnDH;
    @FXML
    private Label lblOrderDH;

    @FXML
    private Region rgnDM1;
    @FXML
    private Label lblOrderDM1;

    @FXML
    private Region rgnDM2;
    @FXML
    private Label lblOrderDM2;

    @FXML
    private Label lblMatchOrderSummary;

    @FXML
    private Button btnOK;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnAuto;


    private Map<Match.EMatchType, Region> mapRegion;
    private Map<Match.EMatchType, Label> mapLabelOrder;

    private final ObservableList<Match.EMatchType> matchOrderTmp = FXCollections.observableArrayList();
    private final List<Match.EMatchType> currentConflictList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Interclub interclub = App.getModelInstance();

        List<MatchOrderUtils.Conflict> conflicts = MatchOrderUtils.getConflicts(interclub);

        // build map region
        mapRegion = ImmutableMap.<Match.EMatchType, Region>builder()
                .put(Match.EMatchType.SH1, rgnSH1)
                .put(Match.EMatchType.SH2, rgnSH2)
                .put(Match.EMatchType.SD1, rgnSD1)
                .put(Match.EMatchType.SD2, rgnSD2)
                .put(Match.EMatchType.DH, rgnDH)
                .put(Match.EMatchType.DD, rgnDD)
                .put(Match.EMatchType.DM1, rgnDM1)
                .put(Match.EMatchType.DM2, rgnDM2)
                .build();
        mapLabelOrder = ImmutableMap.<Match.EMatchType, Label>builder()
                .put(Match.EMatchType.SH1, lblOrderSH1)
                .put(Match.EMatchType.SH2, lblOrderSH2)
                .put(Match.EMatchType.SD1, lblOrderSD1)
                .put(Match.EMatchType.SD2, lblOrderSD2)
                .put(Match.EMatchType.DH, lblOrderDH)
                .put(Match.EMatchType.DD, lblOrderDD)
                .put(Match.EMatchType.DM1, lblOrderDM1)
                .put(Match.EMatchType.DM2, lblOrderDM2)
                .build();

        // build inner model
        Map<Match.EMatchType, ConflictList> map = new HashMap<>();
        Arrays.stream(Match.EMatchType.values()).forEach(type -> map.put(type, new ConflictList(type)));
        conflicts.forEach(conflict -> {
            Conflict c = map.get(conflict.getMatch1().getType()).getOrCreateConflict(conflict);
            map.get(conflict.getMatch2().getType()).add(conflict.getMatch1().getType(), c);
            c.addPlayer(conflict.getPlayer());
        });

        // on match order modification
        matchOrderTmp.addListener((ListChangeListener<? super Match.EMatchType>) c -> {
            // compute conflict list
            currentConflictList.clear();
            if (!matchOrderTmp.isEmpty()) {
                int startIdx = matchOrderTmp.size() - (matchOrderTmp.size() % 2 == 1 ? 3 : 2);
                if (startIdx < 0) startIdx = 0;

                currentConflictList.addAll(matchOrderTmp.stream().skip(startIdx)
                        .map(map::get)
                        .flatMap(list -> list.getValues().keySet().stream())
                        .distinct()
                        .filter(type -> !matchOrderTmp.contains(type))
                        .collect(Collectors.toList()));
            }

            // set opacity
            mapRegion.forEach((matchType, region) -> region.setOpacity((matchOrderTmp.contains(matchType) || currentConflictList.contains(matchType)) ? 0.5 : 1));

            // set label order
            mapLabelOrder.forEach((matchType, label) -> {
                int idx = matchOrderTmp.indexOf(matchType) + 1;
                label.setVisible(idx > 0);
                label.setText(String.valueOf(idx));
            });

            // set button state
            btnOK.setDisable(matchOrderTmp.size() < mapRegion.size());
            btnAuto.setDisable(matchOrderTmp.size() == mapRegion.size());
            btnReset.setDisable(matchOrderTmp.isEmpty());

            // set match order summary
            lblMatchOrderSummary.setText(matchOrderTmp.stream().map(Enum::toString).collect(Collectors.joining(" - ")));
        });

        // setup each region
        mapRegion.forEach((key, value) -> setupRegion(value, map.get(key)));

        // setup btn initial state
        btnOK.setDisable(true);
        btnAuto.setDisable(false);
        btnReset.setDisable(true);
    }

    private void setupRegion(Region rgn, ConflictList conflictList) {
        AtomicBoolean isAdded = new AtomicBoolean(false);
        AtomicBoolean isConflict = new AtomicBoolean(false);

        // setup style
        String style = computeStyle(conflictList);
        rgn.setStyle(style);

        // setup on mouse
        rgn.setOnMouseEntered(ev -> {
            if (!matchOrderTmp.contains(conflictList.getMatchType())) {
                isAdded.set(false);
                isConflict.set(currentConflictList.contains(conflictList.getMatchType())); // must be done before the modification of 'matchOrderTmp'
                matchOrderTmp.add(conflictList.getMatchType());

                if(isConflict.get()) {
                    rgn.getStyleClass().add("match-order-label-rgn-border");
                }
            }
        });

        rgn.setOnMouseExited(ev -> {
            if (!isAdded.get()) {
                matchOrderTmp.remove(conflictList.getMatchType());
                rgn.getStyleClass().remove("match-order-label-rgn-border");
            }
        });

        rgn.setOnMouseClicked(ev -> {
            if (!isAdded.get()
                    && ev.getButton() == MouseButton.PRIMARY
                    && ev.getClickCount() > 1) {
                // on double click
                // add match
                isAdded.set(true);
            }

            if (matchOrderTmp.indexOf(conflictList.getMatchType()) == matchOrderTmp.size() - 1
                    && ev.getButton() == MouseButton.SECONDARY) {
                // remove match
                isAdded.set(false);
            }
        });
    }

    private static String computeStyle(ConflictList conflictList) {
        if (conflictList.isEmpty()) {
            return String.format("-fx-background-color: %s;", colorToCSS(colors.get(0)));
        }

        Map<Match.EMatchType, Conflict> conflicts = conflictList.getValues();

        if (conflicts.size() == 1) {
            Conflict value = conflicts.values().stream().findAny().orElseThrow(() -> new IllegalStateException("Should not pass here..."));
            return String.format("-fx-background-color: %s;", colorToCSS(value.getColor()));
        }

        switch (conflictList.getMatchType()) {
            case DM1:
            case DM2:
                return computeComplexStyleMixted(conflictList);

            case SH1:
                return computeComplexStyleSH1(conflicts);

            case SH2:
                return computeComplexStyleSH2(conflicts);

            case DH:
                return computeComplexStyleDH(conflicts);

            case SD1:
                return computeComplexStyleSD1(conflicts);

            case SD2:
                return computeComplexStyleSD2(conflicts);

            case DD:
                return computeComplexStyleDD(conflicts);

            default:
                throw new IllegalStateException("Unknown match type: " + conflictList.getMatchType());
        }
    }

    private static String computeComplexStyleMixted(ConflictList conflictList) {
        Color nw = null, ne = null, sw = null, se = null;
        Map<Match.EMatchType, Conflict> conflicts = conflictList.getValues();

        if (conflicts.containsKey(Match.EMatchType.SH1)) {
            nw = conflicts.get(Match.EMatchType.SH1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.SH2)) {
            sw = conflicts.get(Match.EMatchType.SH2).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.SD1)) {
            ne = conflicts.get(Match.EMatchType.SD1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.SD2)) {
            se = conflicts.get(Match.EMatchType.SD2).getColor();
        }

        if (conflicts.containsKey(Match.EMatchType.DH)) {
            // at this point, 'nw' or 'sw' should be empty
            if (nw == null && sw == null) {
                if (conflictList.getMatchType() == Match.EMatchType.DM1) {
                    sw = conflicts.get(Match.EMatchType.DH).getColor();
                } else {
                    nw = conflicts.get(Match.EMatchType.DH).getColor();
                }
            } else if (nw == null) {
                nw = conflicts.get(Match.EMatchType.DH).getColor();
            } else {
                sw = conflicts.get(Match.EMatchType.DH).getColor();
            }
        }

        if (conflicts.containsKey(Match.EMatchType.DD)) {
            // at this point, 'ne' or 'se' should be empty
            if (ne == null && se == null) {
                if (conflictList.getMatchType() == Match.EMatchType.DM1) {
                    se = conflicts.get(Match.EMatchType.DH).getColor();
                } else {
                    ne = conflicts.get(Match.EMatchType.DH).getColor();
                }
            }
            if (ne == null) {
                ne = conflicts.get(Match.EMatchType.DD).getColor();
            } else {
                se = conflicts.get(Match.EMatchType.DD).getColor();
            }
        }

        return colorToCSS(nw, sw, se, ne, true);
    }

    private static String computeComplexStyleSH1(Map<Match.EMatchType, Conflict> conflicts) {
        // at this point, 'conflicts' contains 2 values between DH, DM1 or DM2

        Color nw = null, ne = null, sw = null, se;

        if (conflicts.containsKey(Match.EMatchType.DH)) {
            // south west - color from conflicts with DH
            sw = conflicts.get(Match.EMatchType.DH).getColor();

            // south east - color from conflicts with DM (1 or 2)
            se = (conflicts.containsKey(Match.EMatchType.DM1) ? conflicts.get(Match.EMatchType.DM1) : conflicts.get(Match.EMatchType.DM2)).getColor();
        } else {
            // north east - color from conflicts with DM1
            ne = conflicts.get(Match.EMatchType.DM1).getColor();

            // north east - color from conflicts with DM2
            se = conflicts.get(Match.EMatchType.DM2).getColor();
        }


        return colorToCSS(nw, sw, se, ne, false);
    }

    private static String computeComplexStyleSH2(Map<Match.EMatchType, Conflict> conflicts) {
        // at this point, 'conflicts' contains 2 values between DH, DM1 or DM2

        Color nw = null, ne, sw = null, se = null;

        if (conflicts.containsKey(Match.EMatchType.DH)) {
            // north west - color from conflicts with DH
            nw = conflicts.get(Match.EMatchType.DH).getColor();

            // north east - color from conflicts with DM (1 or 2)
            ne = (conflicts.containsKey(Match.EMatchType.DM1) ? conflicts.get(Match.EMatchType.DM1) : conflicts.get(Match.EMatchType.DM2)).getColor();
        } else {
            // north east - color from conflicts with DM1
            ne = conflicts.get(Match.EMatchType.DM1).getColor();

            // north east - color from conflicts with DM2
            se = conflicts.get(Match.EMatchType.DM2).getColor();
        }


        return colorToCSS(nw, sw, se, ne, false);
    }

    private static String computeComplexStyleSD1(Map<Match.EMatchType, Conflict> conflicts) {
        // at this point, 'conflicts' contains 2 values between DD, DM1 or DM2)

        Color nw = null, ne = null, sw, se = null;

        if (conflicts.containsKey(Match.EMatchType.DD)) {
            // south west - color from conflicts with DD
            se = conflicts.get(Match.EMatchType.DD).getColor();

            // south west - color from conflicts with DM (1 or 2)
            sw = (conflicts.containsKey(Match.EMatchType.DM1) ? conflicts.get(Match.EMatchType.DM1) : conflicts.get(Match.EMatchType.DM2)).getColor();
        } else {
            // north west - color from conflicts with DM1
            nw = conflicts.get(Match.EMatchType.DM1).getColor();

            // south west - color from conflicts with DM2
            sw = conflicts.get(Match.EMatchType.DM2).getColor();
        }


        return colorToCSS(nw, sw, se, ne, false);
    }

    private static String computeComplexStyleSD2(Map<Match.EMatchType, Conflict> conflicts) {
        // at this point, 'conflicts' contains 2 values between DD, DM1 or DM2)

        Color nw, ne = null, sw = null, se = null;

        if (conflicts.containsKey(Match.EMatchType.DD)) {
            // north east - color from conflicts with DD
            ne = conflicts.get(Match.EMatchType.DD).getColor();

            // north west - color from conflicts with DM (1 or 2)
            nw = (conflicts.containsKey(Match.EMatchType.DM1) ? conflicts.get(Match.EMatchType.DM1) : conflicts.get(Match.EMatchType.DM2)).getColor();
        } else {
            // north west - color from conflicts with DM1
            nw = conflicts.get(Match.EMatchType.DM1).getColor();

            // north west - color from conflicts with DM2
            sw = conflicts.get(Match.EMatchType.DM2).getColor();
        }

        return colorToCSS(nw, sw, se, ne, false);
    }

    private static String computeComplexStyleDD(Map<Match.EMatchType, Conflict> conflicts) {
        Color nw = null, ne = null, sw = null, se = null;

        if (conflicts.containsKey(Match.EMatchType.SD1)) {
            ne = conflicts.get(Match.EMatchType.SD1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.SD2)) {
            se = conflicts.get(Match.EMatchType.SD2).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.DM1)) {
            nw = conflicts.get(Match.EMatchType.DM1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.DM2)) {
            sw = conflicts.get(Match.EMatchType.DM2).getColor();
        }

        return colorToCSS(nw, sw, se, ne, true);
    }

    private static String computeComplexStyleDH(Map<Match.EMatchType, Conflict> conflicts) {
        Color nw = null, ne = null, sw = null, se = null;

        if (conflicts.containsKey(Match.EMatchType.SH1)) {
            nw = conflicts.get(Match.EMatchType.SH1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.SH2)) {
            sw = conflicts.get(Match.EMatchType.SH2).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.DM1)) {
            ne = conflicts.get(Match.EMatchType.DM1).getColor();
        }
        if (conflicts.containsKey(Match.EMatchType.DM2)) {
            se = conflicts.get(Match.EMatchType.DM2).getColor();
        }

        return colorToCSS(nw, sw, se, ne, true);
    }


    private static String colorToCSS(Color color) {
        if (color == null) return colorToCSS(colors.get(0));
        return String.format(Locale.ENGLISH, "rgb(%d,%d,%d,%.3f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }

    private static String colorToCSS(Color nw, Color sw, Color se, Color ne, boolean isDouble) {
        int from = isDouble ? 36 : 20;
        int start = isDouble ? 60 : 50;
        return String.format("-fx-background-color: " +
                        " %s," +
                        "linear-gradient(from %d%% 100%% to 0%% 0%%, %s %d%%, %s 80%%), " +
                        "linear-gradient(from %d%% 0%% to 0%% 100%%, %s %d%%, %s 80%%), " +
                        "linear-gradient(from %d%% 0%% to 100%% 100%%, %s %d%%, %s 80%%), " +
                        "linear-gradient(from %d%% 100%% to 100%% 0%%, %s %d%%, %s 80%%);",
                colorToCSS(colors.get(0)),
                from, colorToCSS(toTransparent(nw)), start, colorToCSS(nw),
                from, colorToCSS(toTransparent(sw)), start, colorToCSS(sw),
                100 - from, colorToCSS(toTransparent(se)), start, colorToCSS(se),
                100 - from, colorToCSS(toTransparent(ne)), start, colorToCSS(ne));
    }

    private static Color toTransparent(Color color) {
        if (color == null) return toTransparent(colors.get(0));

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.0);
    }

    @FXML
    private void onAutoBtnAction(ActionEvent ev) {

    }

    @FXML
    private void onResetBtnAction(ActionEvent ev) {
        matchOrderTmp.clear();
    }

    @FXML
    private void onOKBtnAction(ActionEvent ev) {
        App.getModelInstance().getMatchOrder().setAll(matchOrderTmp);
        Stage stage = (Stage) rgnSH1.getScene().getWindow();
        stage.close();
    }

    private static class Conflict {
        private final Color color;
        private final List<Player> players = new ArrayList<>();

        private static int nextColorIndex = 1;

        public Conflict() {
            this.color = colors.get(nextColorIndex++);
        }

        private void addPlayer(Player player) {
            players.add(player);
        }

        public List<Player> getPlayers() {
            return players;
        }

        public Color getColor() {
            return color;
        }
    }

    private static class ConflictList {
        private final Match.EMatchType matchType;
        private final Map<Match.EMatchType, Conflict> map = new HashMap<>();

        private ConflictList(Match.EMatchType matchType) {
            this.matchType = matchType;
        }

        private Conflict getOrCreateConflict(MatchOrderUtils.Conflict conflict) {
            Match match = (matchType == conflict.getMatch1().getType() ? conflict.getMatch2() : conflict.getMatch1());
            if (!map.containsKey(match.getType())) {
                map.put(match.getType(), new Conflict());
            }

            return map.get(match.getType());
        }

        public void add(Match.EMatchType matchType, Conflict conflict) {
            map.put(matchType, conflict);
        }

        public Match.EMatchType getMatchType() {
            return matchType;
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }

        public Map<Match.EMatchType, Conflict> getValues() {
            return map;
        }
    }


}
