package me.pancaketaste.dodgebolt.listeners;

import me.pancaketaste.dodgebolt.arena.Arena;
import me.pancaketaste.dodgebolt.arena.ArenaManager;
import me.pancaketaste.dodgebolt.arena.ArenaStatus;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();

        if (entity instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();

                if (e.getEntity() instanceof Player) {
                    Player target = (Player) e.getEntity();

                    Arena arena = ArenaManager.getInstance().getPlayerArena(shooter);

                    if (arena != null) {
                        // Loser
                        target.getInventory().clear();
                        target.sendMessage("§c§lDefeat! §rYou've been shot.");
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
                        target.setGameMode(GameMode.SPECTATOR);

                        // Winner
                        shooter.sendMessage("§e§lVictory! §rYou shot the enemy!");
                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 1.0f, 1.0f);

                        // Increase score
                        if (arena.getBluePlayer() == shooter) {
                            arena.setBlueScore(arena.getBlueScore() + 1);
                        } else {
                            arena.setRedScore(arena.getRedScore() + 1);
                        }

                        arena.setArenaStatus(ArenaStatus.ENDED);
                    }
                }
            }
        }
    }
}
