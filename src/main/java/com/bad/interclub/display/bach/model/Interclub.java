package com.bad.interclub.display.bach.model;

import com.google.common.collect.ImmutableMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interclub {

    private final LocalDate date;

    private final String division;
    private final int pool; // n° de poule

    private final String interclubNb; // n° de journée
    private final int number; // n° de match

    private final String location;

    private final Club host;
    private final Club guest;

    private final String file;

    private final Map<Match.EMatchType, Match> matches;
    private final ObservableList<Match.EMatchType> matchOrder;
    private final ObservableMap<Integer, Match.EMatchType> currentmatches;

    private Interclub(LocalDate date, String division, int pool, String interclubNb, int number, String location, Club host, Club guest, String file, Map<Match.EMatchType, Match> matches, List<Match.EMatchType> matchOrder) {
        this.date = date;
        this.division = division;
        this.pool = pool;
        this.interclubNb = interclubNb;
        this.number = number;
        this.location = location;
        this.host = host;
        this.guest = guest;
        this.file = file;
        this.matches = ImmutableMap.copyOf(matches);
        this.matchOrder = FXCollections.observableArrayList(matchOrder);
        this.currentmatches = FXCollections.observableMap(new HashMap<>());
    }

    public static InterclubBuilder newBuilder() {
        return new InterclubBuilder();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDivision() {
        return division;
    }

    public int getPool() {
        return pool;
    }

    public String getInterclubNb() {
        return interclubNb;
    }

    public int getNumber() {
        return number;
    }

    public Club getHost() {
        return host;
    }

    public Club getGuest() {
        return guest;
    }

    public String getLocation() {
        return location;
    }

    public String getFile() {
        return file;
    }

    public Map<Match.EMatchType, Match> getMatches() {
        return matches;
    }

    public ObservableList<Match.EMatchType> getMatchOrder() {
        return matchOrder;
    }

    public ObservableMap<Integer, Match.EMatchType> getCurrentMatches() {
        return currentmatches;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("date", date)
                .append("division", division)
                .append("pool", pool)
                .append("interclubNb", interclubNb)
                .append("number", number)
                .append("location", location)
                .append("host", host)
                .append("guest", guest)
                .append("file", file)
                .toString();
    }

    public static final class InterclubBuilder {
        private LocalDate date;
        private String division;
        private int pool; // n° de poule
        private String interclubNb; // n° de journée
        private int number; // n° de match
        private String location;
        private Club host;
        private Club guest;
        private String file;
        private final Map<Match.EMatchType, Match> matches = new HashMap<>();
        private List<Match.EMatchType> matchOrder = Collections.emptyList();

        InterclubBuilder() {
        }

        public InterclubBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public InterclubBuilder withDivision(String division) {
            this.division = division;
            return this;
        }

        public InterclubBuilder withPool(int pool) {
            this.pool = pool;
            return this;
        }

        public InterclubBuilder withInterclubNb(String interclubNb) {
            this.interclubNb = interclubNb;
            return this;
        }

        public InterclubBuilder withNumber(int number) {
            this.number = number;
            return this;
        }

        public InterclubBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public InterclubBuilder withHost(Club host) {
            this.host = host;
            return this;
        }

        public InterclubBuilder withGuest(Club guest) {
            this.guest = guest;
            return this;
        }

        public InterclubBuilder withFile(String file) {
            this.file = file;
            return this;
        }

        public InterclubBuilder withMatch(Match match) {
            this.matches.put(match.getType(), match);
            return this;
        }

        public InterclubBuilder withMatchOrder(List<Match.EMatchType> matchOrder) {
            this.matchOrder = matchOrder;
            return this;
        }


        public Interclub build() {
            return new Interclub(date, division, pool, interclubNb, number, location, host, guest, file, matches, matchOrder);
        }
    }
}
