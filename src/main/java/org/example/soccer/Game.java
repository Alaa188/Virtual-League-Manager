package org.example.soccer;

import java.util.*;

public class Game {
    private Team homeTeam;
    private Team awayTeam;
    private int homeScore;
    private int awayScore;
    private List<GameEvent> events;
    private boolean finished;

    public Game(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.events = new ArrayList<>();
        this.finished = false;
    }

    // Getters
    public List<GameEvent> getEvents() { return events; }
    public boolean isFinished() { return finished; }

    public String getResult() {
        return homeTeam.getName() + " " + homeScore + " - " + awayScore + " " + awayTeam.getName();
    }

    public void simulate() {
        events.clear();
        homeScore = 0;
        awayScore = 0;

        Random random = new Random();

        Team kickoffTeam = random.nextBoolean() ? homeTeam : awayTeam;
        events.add(new Kickoff(0, kickoffTeam));

        for (int minute = 1; minute <= 90; minute++) {
            double eventChance = random.nextDouble();

            if (eventChance < 0.02) { // 2% chance of goal
                Team scoringTeam = random.nextBoolean() ? homeTeam : awayTeam;
                Player scorer = scoringTeam.getRandomPlayer();
                if (scorer != null) {
                    events.add(new Goal(minute, scoringTeam, scorer));
                    scorer.addGoal();
                    if (scoringTeam == homeTeam) {
                        homeScore++;
                    } else {
                        awayScore++;
                    }
                }
            } else if (eventChance < 0.05) { // 3% chance of yellow card
                Team cardTeam = random.nextBoolean() ? homeTeam : awayTeam;
                Player cardPlayer = cardTeam.getRandomPlayer();
                if (cardPlayer != null) {
                    events.add(new YellowCard(minute, cardTeam, cardPlayer));
                }
            } else if (eventChance < 0.15) { // 10% chance of possession change
                Team possessionTeam = random.nextBoolean() ? homeTeam : awayTeam;
                Player possessionPlayer = possessionTeam.getRandomPlayer();
                if (possessionPlayer != null) {
                    events.add(new Possession(minute, possessionTeam, possessionPlayer));
                }
            }
        }

        updateTeamStats();
        updatePlayerStats();
        finished = true;
    }

    private void updateTeamStats() {
        if (homeScore > awayScore) {
            homeTeam.recordWin(homeScore, awayScore);
            awayTeam.recordLoss(awayScore, homeScore);
        } else if (awayScore > homeScore) {
            awayTeam.recordWin(awayScore, homeScore);
            homeTeam.recordLoss(homeScore, awayScore);
        } else {
            homeTeam.recordDraw(homeScore, awayScore);
            awayTeam.recordDraw(awayScore, homeScore);
        }
    }

    private void updatePlayerStats() {
        for (Player player : homeTeam.getPlayers()) {
            player.addGame();
        }
        for (Player player : awayTeam.getPlayers()) {
            player.addGame();
        }
    }
}