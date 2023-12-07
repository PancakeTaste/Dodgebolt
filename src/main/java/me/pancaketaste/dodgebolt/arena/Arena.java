package me.pancaketaste.dodgebolt.arena;

import org.bukkit.*;

public class Arena {
    private final String name;
    private final String worldName;
    private ArenaStatus arenaStatus;
    private Location blueSpawn;
    private Location redSpawn;
    private int blueScore;
    private int redScore;
    private int round;

    public Arena(String name) {
        this.name = name;
        this.worldName = "arena_" + name;

        arenaStatus = ArenaStatus.WAITING;

        this.blueScore = 0;
        this.redScore = 0;
        this.round = 0;

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

        return wc.createWorld();
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getRound() {
        return round;
    }

    public void deleteWorld() {
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            Bukkit.unloadWorld(world, false);

            // Delete the world folder
            java.nio.file.Path worldPath = world.getWorldFolder().toPath();
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
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
