package com.bad.interclub.display.bach.model;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;

public final class ScoreUtils {

    private ScoreUtils() {
    }

    public static int getWinner(SetScore setScore) {
        if (setScore.getHostForfeit()) return -1;
        if (setScore.getGuestForfeit()) return 1;

        int host = setScore.getHostPoints();
        int guest = setScore.getGuestPoints();
        if (host > guest) {
            if (host == 30) return 1;
            if (host >= 21 && (host - guest) > 1) return 1;
        } else {
            if (guest == 30) return -1;
            if (guest >= 21 && (guest - host) > 1) return -1;
        }
        return 0;
    }

    public static int getWinner(MatchScore matchScore) {
        if(matchScore.getHostForfeit()) return -1;
        if(matchScore.getGuestForfeit()) return 1;

        if(getWinner(matchScore.getSet2()) != 0) {
            int tmp = getWinner(matchScore.getSet1()) + getWinner(matchScore.getSet2()) + getWinner(matchScore.getSet3());
            if (tmp > 0) {
                return 1;
            } else if(tmp < 0) {
                return -1;
            }
        }
        return 0;
    }

    public static int getWinner(Match match) {
        return getWinner(match.getScore());
    }

    public static ObservableIntegerValue getWinnerProperty(SetScore setScore) {
        return Bindings.createIntegerBinding(() -> getWinner(setScore),
                setScore.hostForfeitProperty(), setScore.guestForfeitProperty(),
                setScore.hostPointsProperty(), setScore.guestPointsProperty());
    }

    public static ObservableIntegerValue getWinnerProperty(MatchScore matchScore) {
        return Bindings.createIntegerBinding(() -> getWinner(matchScore),
                matchScore.hostForfeitProperty(), matchScore.guestForfeitProperty(),
                getWinnerProperty(matchScore.getSet1()), getWinnerProperty(matchScore.getSet2()), getWinnerProperty(matchScore.getSet3()));
    }

    public static ObservableIntegerValue getWinnerProperty(Match match) {
        return getWinnerProperty(match.getScore());
    }

    public static long getHostScore(Interclub interclub) {
        return interclub.getMatches().values().stream()
                .filter(match -> getWinner(match) > 0)
                .count();
    }

    public static long getGuestScore(Interclub interclub) {
        return interclub.getMatches().values().stream()
                .filter(match -> getWinner(match) < 0)
                .count();
    }

    public static ObservableLongValue getHostScoreProperty(Interclub interclub) {
        return Bindings.createLongBinding(() -> getHostScore(interclub),
                interclub.getMatches().values().stream().map(ScoreUtils::getWinnerProperty).toArray(Observable[]::new));
    }

    public static ObservableLongValue getGuestScoreProperty(Interclub interclub) {
        return Bindings.createLongBinding(() -> getGuestScore(interclub),
                interclub.getMatches().values().stream().map(ScoreUtils::getWinnerProperty).toArray(Observable[]::new));
    }

}
