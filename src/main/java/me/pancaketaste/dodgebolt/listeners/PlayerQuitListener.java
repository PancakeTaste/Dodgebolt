package me.pancaketaste.dodgebolt.listeners;

import me.pancaketaste.dodgebolt.arena.Arena;
import me.pancaketaste.dodgebolt.arena.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        // Check if the player is in a game
        Arena arena = ArenaManager.getInstance().getPlayerArena(player);
        if (arena != null) {
            arena.unexpectedStop();
        }

        // Check if the player is in a queue
        Arena queueArena = ArenaManager.getInstance().getPlayerQueueArena(player);
        if (queueArena != null) {
            queueArena.getQueue().dequeue(player);
        }
    }
}
