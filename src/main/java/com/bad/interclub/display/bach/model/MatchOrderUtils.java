package com.bad.interclub.display.bach.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MatchOrderUtils {

    private MatchOrderUtils() {
        // empty constructor
    }

    public static List<Conflict> getConflicts(Interclub interclub) {
        List<Conflict> result = new ArrayList<>();

        // check host players
        for(Player player : interclub.getHost().getPlayers()) {
            List<Match> matches = interclub.getMatches().values().stream()
                    .filter(m -> m.getHost().contains(player))
                    .collect(Collectors.toList());

            if(matches.size() > 1) {
                result.add(new Conflict(matches.get(0), matches.get(1), player));
            }
        }

        // check guest players
        for(Player player : interclub.getGuest().getPlayers()) {
            List<Match> matches = interclub.getMatches().values().stream()
                    .filter(m -> m.getGuest().contains(player))
                    .collect(Collectors.toList());

            if(matches.size() > 1) {
                result.add(new Conflict(matches.get(0), matches.get(1), player));
            }
        }

        // return result
        return result;
    }

    public static List<Match.EMatchType> getBestMatchOrder(Interclub interclub) {
        return getBestMatchOrder(interclub, Collections.emptyList());
    }

    public static List<Match.EMatchType> getBestMatchOrder(Interclub interclub, List<Match.EMatchType> start) {
        List<Conflict> conflicts = getConflicts(interclub);

        // generate all permutations
        List<List<Match.EMatchType>> unsorted = getPermutations_recursive(new HashSet<>(interclub.getMatches().keySet()));

        return unsorted.stream()
                .filter(list -> list.subList(0, start.size()).equals(start))
                .min(Comparator.comparingInt(list -> conflicts.stream()
                        .mapToInt(c -> getDelayOnConflict(list.indexOf(c.getMatch1().getType()), list.indexOf(c.getMatch2().getType())))
                        .sum()))
                .orElse(Collections.emptyList());
    }

    private static int getDelayOnConflict(int idx1, int idx2) {
        // conflict 0/1 or 2/3 or 4/5 or 6/7 --> 2
        // conflict 0-1/2-3 or 2-3/4-5 or 4-5/6-7 --> 1
        // other --> 0
        return Math.max(0, 2 - Math.abs(idx1 / 2 - idx2 / 2));
    }

    private static List<List<Match.EMatchType>> getPermutations_recursive(Set<Match.EMatchType> matches) {
        if(matches.size() == 1) {
            return Collections.singletonList(Collections.singletonList(matches.stream().findAny().get()));
        } else {
            Match.EMatchType type = matches.stream().findAny().get();
            matches.remove(type);
            return getPermutations_recursive(matches).stream()
                    .flatMap(order -> IntStream.range(0, order.size() + 1)
                        .mapToObj(idx -> {
                            List<Match.EMatchType> new_order = new ArrayList<>(order);
                            new_order.add(idx, type);
                            return new_order;
                        }))
                    .collect(Collectors.toList());
        }
    }





    public static class Conflict {
        private Match match1;
        private Match match2;
        private Player player;


        public Conflict(Match match1, Match match2, Player player) {
            this.match1 = match1;
            this.match2 = match2;
            this.player = player;
        }

        public Match getMatch1() {
            return match1;
        }

        public Match getMatch2() {
            return match2;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("match1", match1.getType())
                    .append("match2", match2.getType())
                    .append("player", player.getSurname() + " " + player.getFirstname())
                    .toString();
        }
    }
}
