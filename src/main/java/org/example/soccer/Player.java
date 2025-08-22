package org.example.soccer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Player {
    private final StringProperty name;
    private final StringProperty position;
    private final IntegerProperty goalsScored;
    private final IntegerProperty gamesPlayed;

    public Player(String name, String position) {
        this.name = new SimpleStringProperty(name);
        this.position = new SimpleStringProperty(position);
        this.goalsScored = new SimpleIntegerProperty(0);
        this.gamesPlayed = new SimpleIntegerProperty(0);
    }

    // Property getters for JavaFX binding
    public StringProperty nameProperty() { return name; }
    public StringProperty positionProperty() { return position; }
    public IntegerProperty goalsScoredProperty() { return goalsScored; }
    public IntegerProperty gamesPlayedProperty() { return gamesPlayed; }

    // Regular getters
    public String getName() { return name.get(); }
    public String getPosition() { return position.get(); }
    public int getGoalsScored() { return goalsScored.get(); }
    public int getGamesPlayed() { return gamesPlayed.get(); }

    // Setters
    public void setName(String name) { this.name.set(name); }
    public void setPosition(String position) { this.position.set(position); }
    public void addGoal() { this.goalsScored.set(this.goalsScored.get() + 1); }
    public void addGame() { this.gamesPlayed.set(this.gamesPlayed.get() + 1); }
}
