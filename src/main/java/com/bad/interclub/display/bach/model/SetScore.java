package com.bad.interclub.display.bach.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;

public class SetScore {

    private final IntegerProperty hostPoints;
    private final BooleanProperty hostForfeit;

    private final IntegerProperty guestPoints;
    private final BooleanProperty guestForfeit;

    private SetScore(int hostPoints, boolean hostForfeit, int guestPoints, boolean guestForfeit) {
        this.hostPoints = new SimpleIntegerProperty(hostPoints);
        this.hostForfeit = new SimpleBooleanProperty(hostForfeit);
        this.guestPoints = new SimpleIntegerProperty(guestPoints);
        this.guestForfeit = new SimpleBooleanProperty(guestForfeit);
    }

    public static SetScoreBuilder newBuilder() {
        return new SetScoreBuilder();
    }

    public int getHostPoints() {
        return hostPoints.get();
    }

    public void setHostPoints(int hostPoints) {
        this.hostPoints.set(hostPoints);
    }

    public ObservableIntegerValue hostPointsProperty() {
        return hostPoints;
    }

    public boolean getHostForfeit() {
        return hostForfeit.get();
    }



    public ObservableBooleanValue hostForfeitProperty() {
        return hostForfeit;
    }

    public int getGuestPoints() {
        return guestPoints.get();
    }

    public void setGuestPoints(int guestPoints) {
        this.guestPoints.set(guestPoints);
    }

    public ObservableIntegerValue guestPointsProperty() {
        return guestPoints;
    }

    public boolean getGuestForfeit() {
        return guestForfeit.get();
    }

    public ObservableBooleanValue guestForfeitProperty() {
        return guestForfeit;
    }

    @Override
    public String toString() {
        return String.format("%s/%s",
                String.valueOf(hostPoints.get()) + (hostForfeit.get() ? "f" : ""),
                String.valueOf(guestPoints.get()) + (guestForfeit.get() ? "f" : ""));
    }

    public static final class SetScoreBuilder {
        private int hostPoints;
        private boolean hostForfeit = false;
        private int guestPoints;
        private boolean guestForfeit = false;

        SetScoreBuilder() {
        }

        public SetScoreBuilder withHostPoints(int hostPoints) {
            this.hostPoints = hostPoints;
            return this;
        }

        public SetScoreBuilder withHostForfeit(boolean hostForfeit) {
            this.hostForfeit = hostForfeit;
            return this;
        }

        public SetScoreBuilder withGuestPoints(int guestPoints) {
            this.guestPoints = guestPoints;
            return this;
        }

        public SetScoreBuilder withGuestForfeit(boolean guestForfeit) {
            this.guestForfeit = guestForfeit;
            return this;
        }

        public SetScore build() {
            return new SetScore(hostPoints, hostForfeit, guestPoints, guestForfeit);
        }
    }
}
