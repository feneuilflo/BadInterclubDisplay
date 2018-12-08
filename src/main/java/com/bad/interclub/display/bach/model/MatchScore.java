package com.bad.interclub.display.bach.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchScore {

    private final SetScore set1;
    private final SetScore set2;
    private final SetScore set3;

    private final ObservableBooleanValue hostForfeit;
    private final ObservableBooleanValue guestForfeit;

    private MatchScore(SetScore set1, SetScore set2, SetScore set3, boolean hostForfeit, boolean guestForfeit) {
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
        this.hostForfeit = new SimpleBooleanProperty(hostForfeit);
        this.guestForfeit = new SimpleBooleanProperty(guestForfeit);
    }

    public static MatchScoreBuilder newBuilder() {
        return new MatchScoreBuilder();
    }

    public SetScore getSet1() {
        return set1;
    }

    public SetScore getSet2() {
        return set2;
    }

    public SetScore getSet3() {
        return set3;
    }

    public Boolean getHostForfeit() {
        return hostForfeit.get();
    }

    public ObservableBooleanValue hostForfeitProperty() {
        return hostForfeit;
    }

    public Boolean getGuestForfeit() {
        return guestForfeit.get();
    }

    public ObservableBooleanValue guestForfeitProperty() {
        return guestForfeit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("set1", set1)
                .append("set2", set2)
                .append("set3", set3)
                .append("hostForfeit", hostForfeit.get())
                .append("guestForfeit", guestForfeit.get())
                .toString();
    }

    public static final class MatchScoreBuilder {
        private SetScore set1;
        private SetScore set2;
        private SetScore set3;
        private boolean hostForfeit = false;
        private boolean guestForfeit = false;

        public MatchScoreBuilder withSet1(SetScore set1) {
            this.set1 = set1;
            return this;
        }

        public MatchScoreBuilder withSet2(SetScore set2) {
            this.set2 = set2;
            return this;
        }

        public MatchScoreBuilder withSet3(SetScore set3) {
            this.set3 = set3;
            return this;
        }

        public MatchScoreBuilder withHostForfeit(boolean hostForfeit) {
            this.hostForfeit = hostForfeit;
            return this;
        }

        public MatchScoreBuilder withGuestForfeit(boolean guestForfeit) {
            this.guestForfeit = guestForfeit;
            return this;
        }

        public MatchScore build() {
            return new MatchScore(set1, set2, set3, hostForfeit, guestForfeit);
        }
    }
}
