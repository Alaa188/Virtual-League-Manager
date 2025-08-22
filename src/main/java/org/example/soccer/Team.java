package org.example.soccer;

import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

public class Team {
    private final StringProperty name;
    private final ObservableList<Player> players;
    private final IntegerProperty points;
    private final IntegerProperty goalsFor;
    private final IntegerProperty goalsAgainst;
    private final IntegerProperty wins;
    private final IntegerProperty draws;
    private final IntegerProperty losses;
    private final IntegerProperty gamesPlayed;

    public Team(String name) {
        this.name = new SimpleStringProperty(name);
        this.players = FXCollections.observableArrayList();
        this.points = new SimpleIntegerProperty(0);
        this.goalsFor = new SimpleIntegerProperty(0);
        this.goalsAgainst = new SimpleIntegerProperty(0);
        this.wins = new SimpleIntegerProperty(0);
        this.draws = new SimpleIntegerProperty(0);
        this.losses = new SimpleIntegerProperty(0);
        this.gamesPlayed = new SimpleIntegerProperty(0);
    }

    // Property getters
    public StringProperty nameProperty() { return name; }
    public IntegerProperty pointsProperty() { return points; }
    public IntegerProperty goalsForProperty() { return goalsFor; }
    public IntegerProperty goalsAgainstProperty() { return goalsAgainst; }
    public IntegerProperty winsProperty() { return wins; }
    public IntegerProperty drawsProperty() { return draws; }
    public IntegerProperty lossesProperty() { return losses; }
    public IntegerProperty gamesPlayedProperty() { return gamesPlayed; }

    // Regular getters
    public String getName() { return name.get(); }
    public ObservableList<Player> getPlayers() { return players; }
    public int getPoints() { return points.get(); }
    public int getGoalsFor() { return goalsFor.get(); }
    public int getGoalsAgainst() { return goalsAgainst.get(); }


    public int getGoalDifference() {
        return getGoalsFor() - getGoalsAgainst();
    }


    public void addPlayer(Player player) {
        players.add(player);
    }



    public Player getRandomPlayer() {
        if (players.isEmpty()) return null;
        Random random = new Random();
        return players.get(random.nextInt(players.size()));
    }

    // Match result updates
    public void recordWin(int goalsScored, int goalsConceded) {
        wins.set(wins.get() + 1);
        points.set(points.get() + 3);
        goalsFor.set(goalsFor.get() + goalsScored);
        goalsAgainst.set(goalsAgainst.get() + goalsConceded);
        gamesPlayed.set(gamesPlayed.get() + 1);
    }

    public void recordDraw(int goalsScored, int goalsConceded) {
        draws.set(draws.get() + 1);
        points.set(points.get() + 1);
        goalsFor.set(goalsFor.get() + goalsScored);
        goalsAgainst.set(goalsAgainst.get() + goalsConceded);
        gamesPlayed.set(gamesPlayed.get() + 1);
    }

    public void recordLoss(int goalsScored, int goalsConceded) {
        losses.set(losses.get() + 1);
        goalsFor.set(goalsFor.get() + goalsScored);
        goalsAgainst.set(goalsAgainst.get() + goalsConceded);
        gamesPlayed.set(gamesPlayed.get() + 1);
    }
}
