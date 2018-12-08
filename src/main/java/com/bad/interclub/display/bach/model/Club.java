package com.bad.interclub.display.bach.model;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class Club {

    private final String name;
    private final List<Player> players;

    private Club(String name, List<Player> players) {
        this.name = name;
        this.players = ImmutableList.copyOf(players);
    }

    public static ClubBuilder newBuilder() {
        return new ClubBuilder();
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("players", players)
                .toString();
    }

    public static final class ClubBuilder {
        private String name;
        private List<Player> players = new ArrayList<>();

        ClubBuilder() {
        }

        public ClubBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ClubBuilder withPlayer(Player player) {
            this.players.add(player);
            return this;
        }

        public Club build() {
            return new Club(name, players);
        }
    }
}
