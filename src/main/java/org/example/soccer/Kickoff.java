package org.example.soccer;

public class Kickoff extends GameEvent {
    public Kickoff(int minute, Team team) {
        super(minute, team, null);
    }

    @Override
    public String getDescription() {
        return "org.example.soccer.Kickoff by " + team.getName();
    }
}