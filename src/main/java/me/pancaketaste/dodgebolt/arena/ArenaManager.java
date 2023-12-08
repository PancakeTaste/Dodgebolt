package me.pancaketaste.dodgebolt.arena;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArenaManager {
    private final Map<String, Arena> arenas;
    private static final ArenaManager instance = new ArenaManager();

    private ArenaManager() {
        this.arenas = new HashMap<>();
    }

    public static ArenaManager getInstance() {
        return instance;
    }

    public void addArena(Arena arena) {
        arenas.put(arena.getName(), arena);
    }

    public void removeArena(Arena arena) {
        arenas.remove(arena.getName(), arena);
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    public Arena getWorldArena(String arenaWorld) {
        Optional<Arena> optionalArena = arenas.values()
                .stream()
                .filter(arena -> arena.getWorldName().equals(arenaWorld))
                .findFirst();

        return optionalArena.orElse(null);
    }

    // This function returns the arena where the player is in the game.
    public Arena getPlayerArena(Player player) {
        return arenas.values().stream()
                .filter(arena -> arena.getBluePlayer() == player || arena.getRedPlayer() == player)
                .findFirst()
                .orElse(null);
    }

    // This function returns the arena where the player is in a queue.
    public Arena getPlayerQueueArena(Player player) {
        return arenas.keySet().stream()
                .map(this::getArena)
                .filter(arena -> arena.getQueue().getPlayerPosition(player) != 0)
                .findFirst()
                .orElse(null);
    }
}
