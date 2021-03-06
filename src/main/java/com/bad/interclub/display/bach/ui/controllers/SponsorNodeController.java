package com.bad.interclub.display.bach.ui.controllers;

import com.bad.interclub.display.bach.ui.utils.WrappedImageView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SponsorNodeController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SponsorNodeController.class);

    @FXML
    private Pane node;

    private AtomicReference<Timeline> atTimeline;

    public SponsorNodeController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // create clipping rectangle
        Rectangle rectangle = new Rectangle();
        rectangle.heightProperty().bind(node.heightProperty());
        rectangle.widthProperty().bind(node.widthProperty());
        node.setClip(rectangle);

        // load sponsors images
        try {
            // retrieve files
            File folder = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("sponsors")).toURI());
            List<Image> images = Arrays.stream(Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".png"))))
                    .map(file -> {
                        try {
                            return new Image(file.toURI().toURL().toExternalForm());
                        } catch (Exception e) {
                            LOGGER.error("Failed to load sponsor {} - ex=", file, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // add files from top to bottom

            // node is a stack pane
            // --> every image would be centered by default
            // --> we need to translate each image

            DoubleProperty animationOffset = new SimpleDoubleProperty(0);
            // keep track of current translate value
            // first offset is half of the node
            DoubleBinding offset = animationOffset.add(node.heightProperty().divide(-2)); //
            for (Image image : images) {
                // use custom ImageView for resize purpose
                // this class resize the image to match the node width, and keep the image ratio
                WrappedImageView wrappedImageView = new WrappedImageView(image);
                // translate by offset plus half of the image
                wrappedImageView.translateYProperty().bind(offset.add(wrappedImageView.getHeightProperty().divide(2)));
                // add image
                node.getChildren().add(wrappedImageView);
                // memorize next offset:
                offset = offset.add(wrappedImageView.getHeightProperty());
            }

            // setup animation
            ObservableList<DoubleProperty> heights = FXCollections.observableList(node.getChildren().stream()
                            .filter(node -> node instanceof WrappedImageView)
                            .map(node -> ((WrappedImageView) node).getHeightProperty())
                            .collect(Collectors.toList()),
                    param -> new DoubleProperty[] {param});

            atTimeline = new AtomicReference<>(setupAnimation(animationOffset, 0.0));
            heights.addListener((ListChangeListener<? super DoubleProperty>) c -> {
                double sum = heights.stream()
                        .mapToDouble(ObservableDoubleValue::get)
                        .sum();
                atTimeline.getAndUpdate(old -> {
                    old.stop();
                    Timeline timeline = setupAnimation(animationOffset, sum);
                    timeline.play();
                    return timeline;
                });
            });

            // repeat to fill the blank space that may appear at the end of the animation
            for(int i = 0; i < 2; i++) {
                for (Image image : images) {
                    WrappedImageView wrappedImageView = new WrappedImageView(image);
                    wrappedImageView.translateYProperty().bind(offset.add(wrappedImageView.getHeightProperty().divide(2)));
                    node.getChildren().add(wrappedImageView);
                    offset = offset.add(wrappedImageView.getHeightProperty());
                }
            }

        } catch (URISyntaxException e) {
            LOGGER.error("Failed to load sponsors - ex=", e);
        }
    }

    private Timeline setupAnimation(DoubleProperty animationOffset, double target) {
        LOGGER.info("Creating sponsors animation with max value {}", target);

        // restart
        animationOffset.set(0.0);

        // create timeline
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), new KeyValue(animationOffset, -1.0 * target))
        );

        // loop
        timeline.setCycleCount(Timeline.INDEFINITE);

        return timeline;
    }
}
