package org.example.soccer;

import javafx.collections.*;

public class League {
    private String name;
    private ObservableList<Team> teams;
    private ObservableList<Game> games;
    private ObservableList<GameEvent> allEvents;

    public League(String name) {
        this.name = name;
        this.teams = FXCollections.observableArrayList();
        this.games = FXCollections.observableArrayList();
        this.allEvents = FXCollections.observableArrayList();
    }

    public String getName() { return name; }
    public ObservableList<Team> getTeams() { return teams; }
    public ObservableList<Game> getGames() { return games; }
    public ObservableList<GameEvent> getAllEvents() { return allEvents; }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }

    // Generate all possible matches (round-robin)
    public void generateMatches() {
        games.clear();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                games.add(new Game(teams.get(i), teams.get(j)));
            }
        }
    }

    // Simulate all matches
    public void simulateAllMatches() {
        allEvents.clear();
        for (Game game : games) {
            game.simulate();
            allEvents.addAll(game.getEvents());
        }
        sortTeamsByPoints();
    }

    // Simulate next match
    public Game simulateNextMatch() {
        for (Game game : games) {
            if (!game.isFinished()) {
                game.simulate();
                allEvents.addAll(game.getEvents());
                sortTeamsByPoints();
                return game;
            }
        }
        return null;
    }

    private void sortTeamsByPoints() {
        teams.sort((t1, t2) -> {
            int pointsCompare = Integer.compare(t2.getPoints(), t1.getPoints());
            if (pointsCompare != 0) return pointsCompare;

            int goalDiffCompare = Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
            if (goalDiffCompare != 0) return goalDiffCompare;

            return Integer.compare(t2.getGoalsFor(), t1.getGoalsFor());
        });
    }

    public boolean isSeasonComplete() {
        return games.stream().allMatch(Game::isFinished);
    }

    public void resetSeason() {
        // Reset all team stats
        for (Team team : teams) {
            team.pointsProperty().set(0);
            team.goalsForProperty().set(0);
            team.goalsAgainstProperty().set(0);
            team.winsProperty().set(0);
            team.drawsProperty().set(0);
            team.lossesProperty().set(0);
            team.gamesPlayedProperty().set(0);

            // Reset player stats
            for (Player player : team.getPlayers()) {
                player.goalsScoredProperty().set(0);
                player.gamesPlayedProperty().set(0);
            }
        }


        games.clear();
        allEvents.clear();
    }
}