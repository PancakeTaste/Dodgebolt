package me.pancaketaste.dodgebolt.arena;

import me.pancaketaste.dodgebolt.Dodgebolt;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Arena {
    private final String name;
    private final String worldName;
    private ArenaStatus arenaStatus;
    private Player bluePlayer;
    private Player redPlayer;
    private Location blueSpawn;
    private Location redSpawn;
    private final ArenaQueue arenaQueue;

    public Arena(String name) {
        this.name = name;
        this.worldName = "arena_" + name;
        this.arenaStatus = ArenaStatus.WAITING;
        this.arenaQueue = new ArenaQueue();

        ArenaManager.getInstance().addArena(this);
    }

    public void delete() {
        deleteWorld();
        ArenaManager.getInstance().removeArena(this);
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public World createWorld() {
        WorldCreator wc = new WorldCreator(worldName);
        wc.environment(World.Environment.NORMAL).type(WorldType.NORMAL);

        World world = wc.createWorld();

        world.setGameRuleValue("showDeathMessages", "false");

        return world;
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public Player getBluePlayer() {
        return bluePlayer;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public ArenaQueue getQueue() {
        return arenaQueue;
    }

    public void deleteWorld() {
        World arenaWorld = Bukkit.getWorld(worldName);

        if (arenaWorld != null) {
            Bukkit.unloadWorld(arenaWorld, false);

            // Delete the world folder
            java.nio.file.Path worldPath = arenaWorld.getWorldFolder().toPath();
            try {
                java.nio.file.Files.walk(worldPath)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(java.io.File::delete);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setArenaStatus(ArenaStatus arenaStatus) {
        this.arenaStatus = arenaStatus;

        if (arenaStatus == ArenaStatus.STARTING) {
            arenaQueue.dequeue(bluePlayer);
            arenaQueue.dequeue(redPlayer);
            arenaQueue.sendPlayersPositions();

            bluePlayer.sendMessage("Your enemy is " + ChatColor.RED + redPlayer.getDisplayName() + ChatColor.WHITE + ".");
            redPlayer.sendMessage("Your enemy is " + ChatColor.BLUE + bluePlayer.getDisplayName() + ChatColor.WHITE + ".");

            bluePlayer.setGameMode(GameMode.SURVIVAL);
            redPlayer.setGameMode(GameMode.SURVIVAL);

            bluePlayer.setHealth(20);
            redPlayer.setHealth(20);

            bluePlayer.getInventory().clear();
            redPlayer.getInventory().clear();

            bluePlayer.teleport(blueSpawn);
            redPlayer.teleport(redSpawn);

            // Countdown
            Plugin plugin = Dodgebolt.getPlugin(Dodgebolt.class);
            final int[] countdown = {6};

            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (countdown[0] == 3) {
                    bluePlayer.sendMessage("The game starts in " + ChatColor.YELLOW + "3" + ChatColor.WHITE + " seconds!");
                    redPlayer.sendMessage("The game starts in " + ChatColor.YELLOW + "3" + ChatColor.WHITE + " seconds!");
                }
                if (countdown[0] > 0 && countdown[0] <= 3) {
                    bluePlayer.playSound(bluePlayer.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
                    redPlayer.playSound(redPlayer.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
                } else if (countdown[0] == 0) {
                    bluePlayer.playSound(bluePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    redPlayer.playSound(redPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                    // Give the bow and arrows to the players
                    ItemStack BowItem = new ItemStack(Material.BOW);
                    bluePlayer.getInventory().addItem(BowItem);
                    redPlayer.getInventory().addItem(BowItem);
                    ItemStack ArrowsItem = new ItemStack(Material.ARROW, 3);
                    bluePlayer.getInventory().addItem(ArrowsItem);
                    redPlayer.getInventory().addItem(ArrowsItem);

                    setArenaStatus(ArenaStatus.IN_PROGRESS);
                }
                countdown[0]--;
            }, 0L, 20L);
        }

        if (arenaStatus == ArenaStatus.ENDED) {
            // End in 3 seconds
            Plugin plugin = Dodgebolt.getPlugin(Dodgebolt.class);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                bluePlayer.setGameMode(GameMode.SURVIVAL);
                redPlayer.setGameMode(GameMode.SURVIVAL);

                bluePlayer.setHealth(20);
                redPlayer.setHealth(20);

                bluePlayer.getInventory().clear();
                redPlayer.getInventory().clear();

                World world = Bukkit.getWorld("world");
                bluePlayer.teleport(world.getSpawnLocation());
                redPlayer.teleport(world.getSpawnLocation());

                setArenaStatus(ArenaStatus.WAITING);
            }, 60L);
        }
    }

    public void startGame() {
        List<Player> firstTwoPlayers = arenaQueue.getFirstTwoPlayers();
        setBluePlayer(firstTwoPlayers.get(0));
        setRedPlayer(firstTwoPlayers.get(1));
        setArenaStatus(ArenaStatus.STARTING);
    }

    public void setBluePlayer(Player bluePlayer) {
        this.bluePlayer = bluePlayer;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
    }
}
