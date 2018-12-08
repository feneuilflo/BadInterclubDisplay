package com.bad.interclub.display.bach.model;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class Match {

    private final EMatchType type;

    private final List<Player> host;
    private final List<Player> guest;

    private final MatchScore score;

    public enum EMatchType {
        SH1, SH2, SD1, SD2, DH, DD, DM1, DM2;
    }

    private Match(EMatchType type, List<Player> host, List<Player> guest, MatchScore score) {
        this.type = type;
        this.host = ImmutableList.copyOf(host);
        this.guest = ImmutableList.copyOf(guest);
        this.score = score;
    }

    public static MatchBuilder newBuilder() {
        return new MatchBuilder();
    }

    public EMatchType getType() {
        return type;
    }

    public List<Player> getHost() {
        return host;
    }

    public List<Player> getGuest() {
        return guest;
    }

    public MatchScore getScore() {
        return score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("host", host)
                .append("guest", guest)
                .append("score", score)
                .toString();
    }

    public static final class MatchBuilder {
        private EMatchType type;
        private final List<Player> host = new ArrayList<>();
        private List<Player> guest = new ArrayList<>();
        private MatchScore score;

        MatchBuilder() {
        }

        public MatchBuilder withType(EMatchType type) {
            this.type = type;
            return this;
        }

        public MatchBuilder withHostPlayer(Player host) {
            this.host.add(host);
            return this;
        }

        public MatchBuilder withGuestPlayer(Player guest) {
            this.guest.add(guest);
            return this;
        }

        public MatchBuilder withScore(MatchScore score) {
            this.score = score;
            return this;
        }

        public Match build() {
            return new Match(type, host, guest, score);
        }
    }
}
