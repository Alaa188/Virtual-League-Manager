package org.example.soccer;

public abstract class GameEvent {
    protected Player player;
    protected Team team;
    protected int minute;

    public GameEvent(int minute, Team team, Player player) {
        this.minute = minute;
        this.team = team;
        this.player = player;
    }

    public abstract String getDescription();
    public int getMinute() { return minute; }
    public Team getTeam() { return team; }
    public Player getPlayer() { return player; }

    @Override
    public String toString() {
        return String.format("%d': %s", minute, getDescription());
    }
}