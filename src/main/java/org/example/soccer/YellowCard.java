package org.example.soccer;

public class YellowCard extends GameEvent {
    public YellowCard(int minute, Team team, Player player) {
        super(minute, team, player);
    }

    @Override
    public String getDescription() {
        return " Yellow Card for " + player.getName() + " (" + team.getName() + ")";
    }
}