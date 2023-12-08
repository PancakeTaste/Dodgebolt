package me.pancaketaste.dodgebolt.arena;

import me.pancaketaste.dodgebolt.Dodgebolt;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.tozymc.spigot.api.title.TitleApi;

import java.util.List;

public class Arena {
    private Dodgebolt dodgeboltPlugin;
    private final String name;
    private final String worldName;
    private ArenaStatus arenaStatus;
    private Player bluePlayer;
    private Player redPlayer;
    private Location blueSpawn;
    private Location redSpawn;
    private final ArenaQueue arenaQueue;
    private int blueScore;
    private int redScore;
    private int rounds;

    public Arena(Dodgebolt dodgeboltPlugin, String name, String worldName) {
        this.dodgeboltPlugin = dodgeboltPlugin;

        this.name = name;
        this.worldName = worldName;
        this.arenaStatus = ArenaStatus.WAITING;
        this.arenaQueue = new ArenaQueue();

        ArenaManager.getInstance().addArena(this);
    }

    public void create(Dodgebolt dodgeboltPlugin) {
        // Insert into the database
        this.dodgeboltPlugin.getDatabase().insertArena(getName(), getWorldName());
    }

    public void delete() {
        deleteWorld();
        ArenaManager.getInstance().removeArena(this);

        // Delete from the database
        this.dodgeboltPlugin.getDatabase().deleteArena(getName());
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

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public ArenaQueue getQueue() {
        return arenaQueue;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getRounds() {
        return rounds;
    }

    public void deleteWorld() {
        World arenaWorld = Bukkit.getWorld(worldName);

        if (arenaWorld != null) {
            Bukkit.unloadWorld(arenaWorld, false);
            deleteWorldFolder(arenaWorld.getWorldFolder().toPath());
        }
    }

    private void deleteWorldFolder(java.nio.file.Path worldPath) {
        try {
            java.nio.file.Files.walk(worldPath)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(java.io.File::delete);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void setArenaStatus(ArenaStatus arenaStatus) {
        this.arenaStatus = arenaStatus;

        if (arenaStatus == ArenaStatus.WAITING) {
            startGame();
        }

        if (arenaStatus == ArenaStatus.STARTING) {
            initializeGame();
        }

        if (arenaStatus == ArenaStatus.ENDED) {
            setRounds(getRounds() + 1);

            Plugin plugin = Dodgebolt.getPlugin(Dodgebolt.class);
            if (getRounds() >= 3) {
                Bukkit.getScheduler().runTaskLater(plugin, this::stopGame, 60L);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> setArenaStatus(ArenaStatus.STARTING), 60L);
            }
        }
    }

    private void initializeGame() {
        if (getRounds() == 0) {
            arenaQueue.dequeue(bluePlayer);
            arenaQueue.dequeue(redPlayer);
            arenaQueue.sendPlayersPositions();

            sendPlayersMessages();
        }
        cleanupPlayers();

        bluePlayer.teleport(blueSpawn);
        redPlayer.teleport(redSpawn);

        startCountdown();
    }

    private void sendPlayersMessages() {
        bluePlayer.sendMessage("Your enemy is " + ChatColor.RED + redPlayer.getDisplayName() + ChatColor.WHITE + ".");
        redPlayer.sendMessage("Your enemy is " + ChatColor.BLUE + bluePlayer.getDisplayName() + ChatColor.WHITE + ".");
    }

    private void startCountdown() {
        Plugin plugin = Dodgebolt.getPlugin(Dodgebolt.class);
        final int[] countdown = (getRounds() == 0) ? new int[]{10} : new int[]{3};

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdown[0] > 0 && countdown[0] <= 3) {
                sendCountdownTitle(countdown[0]);

                bluePlayer.playSound(bluePlayer.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
                redPlayer.playSound(redPlayer.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
            } else if (countdown[0] == 0) {
                // Give bow and arrows
                ItemStack bowItem = new ItemStack(Material.BOW);
                ItemStack arrowsItem = new ItemStack(Material.ARROW, 32);

                bluePlayer.getInventory().addItem(bowItem, arrowsItem);
                redPlayer.getInventory().addItem(bowItem, arrowsItem);

                //
                setArenaStatus(ArenaStatus.IN_PROGRESS);

                // Play level up sound
                bluePlayer.playSound(bluePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                redPlayer.playSound(redPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            countdown[0]--;
        }, 0L, 20L);
    }

    private void sendCountdownTitle(int count) {
        String countdownColor = getCountdownColor(count);
        TitleApi.sendTitle(bluePlayer, countdownColor + count, "", 2, 16, 2);
        TitleApi.sendTitle(redPlayer, countdownColor + count, "", 2, 16, 2);
    }

    private String getCountdownColor(int count) {
        String countdownColor = "§a§l";
        if (count == 1) countdownColor = "§c§l";
        else if (count == 2) countdownColor = "§e§l";
        return countdownColor;
    }

    public void startGame() {
        List<Player> firstTwoPlayers = arenaQueue.getFirstTwoPlayers();

        if (firstTwoPlayers.size() >= 2) {
            setBluePlayer(firstTwoPlayers.get(0));
            setRedPlayer(firstTwoPlayers.get(1));
            setArenaStatus(ArenaStatus.STARTING);
        }
    }

    public void stopGame() {
        // Announce the winner
        if (getRounds() >= 3) {
            if (getBlueScore() > getRedScore()) {
                bluePlayer.sendMessage(ChatColor.BLUE + bluePlayer.getDisplayName() + ChatColor.WHITE + " is the winner!");
                redPlayer.sendMessage(ChatColor.BLUE + bluePlayer.getDisplayName() + ChatColor.WHITE + " is the winner!");
            } else {
                bluePlayer.sendMessage(ChatColor.RED + redPlayer.getDisplayName() + ChatColor.WHITE + " is the winner!");
                redPlayer.sendMessage(ChatColor.RED + redPlayer.getDisplayName() + ChatColor.WHITE + " is the winner!");
            }
        }

        setBlueScore(0);
        setRedScore(0);
        setRounds(0);

        cleanupPlayers();

        World world = Bukkit.getWorld("world");
        bluePlayer.teleport(world.getSpawnLocation());
        redPlayer.teleport(world.getSpawnLocation());

        setBluePlayer(null);
        setRedPlayer(null);

        setArenaStatus(ArenaStatus.WAITING);
    }

    private void cleanupPlayers() {
        bluePlayer.setGameMode(GameMode.SURVIVAL);
        redPlayer.setGameMode(GameMode.SURVIVAL);

        bluePlayer.setHealth(20);
        redPlayer.setHealth(20);

        // Clear inventory
        bluePlayer.getInventory().clear();
        redPlayer.getInventory().clear();
    }

    public void unexpectedStop() {
        String message = ChatColor.RED + "Your enemy disconnected. The match has been canceled.";
        bluePlayer.sendMessage(message);
        redPlayer.sendMessage(message);

        stopGame();
    }


    public void setBluePlayer(Player bluePlayer) {
        this.bluePlayer = bluePlayer;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
        saveSpawns();
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
        saveSpawns();
    }

    // Update team spawn points in the database
    private void saveSpawns() {
        this.dodgeboltPlugin.getDatabase().updateArenaSpawns(getName(), getBlueSpawn(), getRedSpawn());
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }
}
