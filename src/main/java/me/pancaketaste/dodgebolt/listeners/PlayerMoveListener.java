package me.pancaketaste.dodgebolt.listeners;

import me.pancaketaste.dodgebolt.arena.Arena;
import me.pancaketaste.dodgebolt.arena.ArenaManager;
import me.pancaketaste.dodgebolt.arena.ArenaStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaManager.getInstance().getPlayerArena(player);

        if (arena != null) {
            if (arena.getArenaStatus() == ArenaStatus.STARTING) {
                e.setCancelled(true);
            }
        }
    }
}
