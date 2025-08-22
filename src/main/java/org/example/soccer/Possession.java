package org.example.soccer;

public class Possession extends GameEvent {
    public Possession(int minute, Team team, Player player) {
        super(minute, team, player);
    }

    @Override
    public String getDescription() {
        return   player.getName() + " has possession for " + team.getName();
    }
}