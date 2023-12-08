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
                Player shooterPlayer = (Player) arrow.getShooter();

                if (e.getEntity() instanceof Player) {
                    Player targetPlayer = (Player) e.getEntity();

                    Arena shooterArena = ArenaManager.getInstance().getPlayerArena(shooterPlayer);

                    // Check if the shooter is in an arena
                    if (shooterArena != null) {
                        if (shooterArena.getArenaStatus() == ArenaStatus.IN_PROGRESS) {
                            targetPlayer.setGameMode(GameMode.SPECTATOR);
                            targetPlayer.sendMessage("§c§lDefeat! §rYou've been shot.");
                            targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);

                            shooterPlayer.sendMessage("§e§lCongrats! §rYou shot the enemy!");
                            shooterPlayer.playSound(shooterPlayer.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 1.0f, 1.0f);

                            shooterArena.setArenaStatus(ArenaStatus.ENDED);
                        }
                    }
                }
            }
        }
    }
}
