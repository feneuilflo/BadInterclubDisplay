package com.bad.interclub.display.bach.ui.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Utility class: Resize an image to match the parent width and keep the image ratio.
 * <p>
 *     The image height is available through {@link WrappedImageView#getHeightProperty()}
 * </p>
 */
public class WrappedImageView extends ImageView {

    private final DoubleProperty heightProperty = new SimpleDoubleProperty();

    public WrappedImageView(Image image) {
        super(image);
        setPreserveRatio(true);
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double prefWidth(double height) {
        return getImage().getWidth();
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefHeight(double width) {
        return getImage().getHeight();
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setFitWidth(width);
        double new_height = getImage().getHeight() * width / getImage().getWidth();
        heightProperty.set(new_height);
    }

    public DoubleProperty getHeightProperty() {
        return heightProperty;
    }
}
