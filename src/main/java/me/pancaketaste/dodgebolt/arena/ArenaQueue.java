package me.pancaketaste.dodgebolt.arena;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;

public class ArenaQueue {
    private final List<Player> playerQueue;

    public ArenaQueue() {
        this.playerQueue = new ArrayList<>();
    }

    public void enqueue(Player player) {
        playerQueue.add(player);
    }

    public void dequeue(Player player) {
        playerQueue.remove(player);
    }

    public int getPlayerPosition(Player player) {
        return playerQueue.indexOf(player) + 1;
    }

    public List<Player> getFirstTwoPlayers() {
        List<Player> firstTwoPlayers = new ArrayList<>();

        if (playerQueue.size() >= 2) {
            firstTwoPlayers.add(playerQueue.get(0));
            firstTwoPlayers.add(playerQueue.get(1));
        }

        return firstTwoPlayers;
    }

    public void sendPlayersPositions() {
        // No players in the queue
        if (playerQueue.isEmpty()) {
            return;
        }

        for (Player player : playerQueue) {
            int queuePosition = getPlayerPosition(player);
            player.sendMessage("Now you are in position " + ChatColor.YELLOW + queuePosition + ChatColor.WHITE + ".");
        }
    }
}
