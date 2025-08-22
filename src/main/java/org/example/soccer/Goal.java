package org.example.soccer;

public class Goal extends GameEvent {
    public Goal(int minute, Team team, Player player) {
        super(minute, team, player);
    }

    @Override
    public String getDescription() {
        return "⚽ GOAL! " + player.getName() + " scores for " + team.getName();
    }
}