package me.pancaketaste.dodgebolt;

import me.pancaketaste.dodgebolt.arena.Arena;
import me.pancaketaste.dodgebolt.arena.ArenaManager;
import me.pancaketaste.dodgebolt.arena.ArenaStatus;
import me.pancaketaste.dodgebolt.arena.ArenaQueue;
import me.pancaketaste.dodgebolt.listeners.EntityDamageByEntityListener;
import me.pancaketaste.dodgebolt.listeners.PlayerJoinListener;
import me.pancaketaste.dodgebolt.listeners.PlayerMoveListener;
import me.pancaketaste.dodgebolt.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class Dodgebolt extends JavaPlugin {
    public static final String PERMISSION_ARENA_CREATE = "dodgebolt.arena.create";
    public static final String PERMISSION_ARENA_TP = "dodgebolt.arena.tp";
    public static final String PERMISSION_ARENA_SET_SPAWN = "dodgebolt.arena.setspawn";
    public static final String PERMISSION_ARENA_DELETE = "dodgebolt.arena.delete";

    @Override
    public void onEnable() {
        // Events
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("dodgebolt")) {
                // No args
                if (args.length == 0) {
                    sendHelp(player);
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "join":
                        handleJoinCommand(player, args);
                        break;
                    case "leave":
                        handleLeaveCommand(player);
                        break;
                    case "arena": {
                        if (args.length == 1) {
                            sendHelp(player, "arena");
                            return true;
                        }

                        switch (args[1].toLowerCase()) {
                            case "create": {
                                handleArenaCreateCommand(player, args);
                                break;
                            }
                            case "list": {
                                handleArenaListCommand(player);
                                break;
                            }
                            case "tp": {
                                handleArenaTpCommand(player, args);
                                break;
                            }
                            case "setspawn": {
                                handleSetSpawnCommand(player, args);
                                break;
                            }
                            case "delete": {
                                handleArenaDeleteCommand(player, args);
                                break;
                            }
                            default: {
                                sendHelp(player, "arena");
                            }
                        }
                        break;
                    }
                    default: {
                        sendHelp(player);
                    }
                }
            }
        } else {
            sender.sendMessage("This command can only be executed by players.");
        }

        return false;
    }

    private void handleJoinCommand(Player player, String[] args) {
        if (args.length == 1) {
            sendHelp(player, "join");
            return;
        }

        // Check if the player is in a game
        if (ArenaManager.getInstance().getPlayerArena(player) != null) {
            player.sendMessage(ChatColor.RED + "You are in a game.");
            return;
        }

        // Check if the player is in a queue
        Arena queueArena = ArenaManager.getInstance().getPlayerQueueArena(player);
        if (queueArena != null) {
            player.sendMessage(ChatColor.RED + "You are already in a queue. Leave it first.");
            return;
        }

        String arenaName = args[1];
        Arena arena = ArenaManager.getInstance().getArena(arenaName);

        // Check if the arena doesn't exist
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "This arena doesn't exist.");
            return;
        }

        // Check if the team spawn points are set
        if (arena.getBlueSpawn() == null || arena.getRedSpawn() == null) {
            player.sendMessage(ChatColor.RED + "The arena is currently not configured. The team spawn points have not been established.");
            return;
        }

        // Add the player in the arena queue
        ArenaQueue arenaQueue = arena.getQueue();
        arenaQueue.enqueue(player);

        int queuePosition = arenaQueue.getPlayerPosition(player);
        player.sendMessage("You have been added to the waiting queue.");
        player.sendMessage("You are in position " + ChatColor.YELLOW + queuePosition + ChatColor.WHITE + ".");

        // Begin the game only if there are two players waiting and the arena is empty.
        if (queuePosition == 2) {
            if (arena.getArenaStatus() == ArenaStatus.WAITING) {
                arena.startGame();
            }
        }
    }

    private void handleLeaveCommand(Player player) {
        Arena queueArena = ArenaManager.getInstance().getPlayerQueueArena(player);

        // Check if the player is not in a queue
        if (queueArena == null) {
            player.sendMessage(ChatColor.RED + "You are not in a queue.");
            return;
        }

        queueArena.getQueue().dequeue(player);
        player.sendMessage("You have been excluded from the queue.");
    }

    private void handleArenaCreateCommand(Player player, String[] args) {
        // Permission
        if (!player.hasPermission(PERMISSION_ARENA_CREATE)) {
            sendNoPerm(player);
            return;
        }

        if (args.length == 2) {
            sendHelp(player, "create");
            return;
        }

        String arenaName = args[2];

        // Check if an arena with the same name already exists
        if (ArenaManager.getInstance().getArena(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena with the same name already exists.");
            return;
        }

        player.sendMessage("Creating the arena...");

        // Save the arena and create the world
        Arena arena = new Arena(arenaName);
        World arenaWorld = arena.createWorld();

        player.sendMessage(ChatColor.GREEN + "Successfully created.");
        player.teleport(arenaWorld.getSpawnLocation());
    }

    private void handleArenaListCommand(Player player) {
        Collection<Arena> arenas = ArenaManager.getInstance().getAllArenas();

        if (arenas.isEmpty()) {
            player.sendMessage("There are no arenas.");
            return;
        }

        player.sendMessage("List of arenas:");
        for (Arena arena : arenas) {
            ArenaStatus arenaStatus = arena.getArenaStatus();
            String arenaStatusDisplay = ChatColor.GRAY + "Waiting for players";

            if (arenaStatus == ArenaStatus.STARTING || arenaStatus == ArenaStatus.IN_PROGRESS || arenaStatus == ArenaStatus.ENDED) {
                arenaStatusDisplay = ChatColor.RED + "Occupied";
            }

            player.sendMessage( ChatColor.YELLOW + arena.getName() + ChatColor.WHITE + " - " + arenaStatusDisplay);
        }
    }

    private void handleArenaTpCommand(Player player, String[] args) {
        // Permission
        if (!player.hasPermission(PERMISSION_ARENA_TP)) {
            sendNoPerm(player);
            return;
        }

        if (args.length == 2) {
            sendHelp(player, "tp");
            return;
        }

        String arenaName = args[2];
        Arena arena = ArenaManager.getInstance().getArena(arenaName);

        // Check if the arena doesn't exist
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "This arena doesn't exist.");
            return;
        }

        World arenaWorld = Bukkit.getWorld(arena.getWorldName());
        player.teleport(arenaWorld.getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "You've been teleported.");
    }

    private void handleSetSpawnCommand(Player player, String[] args) {
        // Permission
        if (!player.hasPermission(PERMISSION_ARENA_SET_SPAWN)) {
            sendNoPerm(player);
            return;
        }

        if (args.length == 2) {
            sendHelp(player, "setspawn");
            return;
        }

        String teamName = args[2];
        World playerWorld = player.getWorld();
        Arena arena = ArenaManager.getInstance().getWorldArena(playerWorld.getName()); // Get the arena by the world name

        // Check if the player is not in an arena
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "You are not in an arena.");
            return;
        }

        if (teamName.equalsIgnoreCase("blue")) {
            arena.setBlueSpawn(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "You set the spawn point for " + ChatColor.BLUE + "blue" + ChatColor.GREEN + " team to your current location.");
        } else if (teamName.equalsIgnoreCase("red")) {
            arena.setRedSpawn(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "You set the spawn point for " + ChatColor.RED + "red" + ChatColor.GREEN + " team to your current location.");
        } else {
            // Invalid team name
            sendHelp(player, "setspawn");
        }
    }

    private void handleArenaDeleteCommand(Player player, String[] args) {
        // Permission
        if (!player.hasPermission(PERMISSION_ARENA_DELETE)) {
            sendNoPerm(player);
            return;
        }

        if (args.length == 2) {
            sendHelp(player, "delete");
            return;
        }

        String arenaName = args[2];
        Arena arena = ArenaManager.getInstance().getArena(arenaName);

        // Check if the arena doesn't exist
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "This arena doesn't exist.");
            return;
        }

        // Check if the arena is occupied
        if (!arena.getArenaStatus().equals(ArenaStatus.WAITING)) {
            player.sendMessage(ChatColor.RED + "The arena in currently in use.");
            return;
        }

        // Check if there are players in the world
        World arenaWorld = Bukkit.getWorld(arena.getWorldName());
        if (!arenaWorld.getPlayers().isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are players in the arena world.");
            return;
        }

        // Delete the arena
        arena.delete();

        player.sendMessage(ChatColor.GREEN + "Successfully deleted.");
    }

    private void sendHelp(Player player, String... specificCommands) {
        if (specificCommands.length == 0) {
            sendGenericHelp(player);
        } else {
            for (String command : specificCommands) {
                sendSpecificHelp(player, command);
            }
        }
    }

    private void sendGenericHelp(Player player) {
        player.sendMessage(ChatColor.WHITE + "Dodgebolt Help:");
        sendCommandHelp(player, "join", "<arena>", "Join queue for the specified arena.");
        sendCommandHelp(player, "leave", "", "Leave the current queue.");
        sendCommandHelp(player, "arena", "", "Manage arenas.");
    }

    private void sendSpecificHelp(Player player, String command) {
        switch (command.toLowerCase()) {
            case "join":
                sendCommandHelp(player, "join", "<arena>", "Join queue for the specified arena.");
                break;
            case "leave":
                sendCommandHelp(player, "leave", "", "Leave the current queue.");
                break;
            case "arena":
                sendArenaHelp(player);
                break;
            case "create":
                sendCommandHelp(player, "arena create", "<name>", "Create an arena with the specified name.");
                break;
            case "list":
                sendCommandHelp(player, "arena list", "", "List all arenas.");
                break;
            case "tp":
                sendCommandHelp(player, "arena tp", "<name>", "Teleport to the specified arena.");
                break;
            case "setspawn":
                sendCommandHelp(player, "arena setspawn", "<team [red|blue]>", "Set the spawn location for the specified team.");
                break;
            case "delete":
                sendCommandHelp(player, "arena delete", "<name>", "Delete the specified arena.");
                break;
        }
    }

    private void sendCommandHelp(Player player, String command, String arg, String desc) {
        String spacedArg = arg;
        if (!arg.isEmpty()) spacedArg = " " + arg;

        player.sendMessage(ChatColor.YELLOW + "/dodgebolt " + command + ChatColor.WHITE + spacedArg + " - " + desc);
    }

    private void sendArenaHelp(Player player) {
        sendCommandHelp(player, "arena list", "", "List all arenas.");
        if (player.hasPermission(PERMISSION_ARENA_CREATE)) {
            sendCommandHelp(player, "arena create", "<name>", "Create an arena with the specified name.");
        }
        if (player.hasPermission(PERMISSION_ARENA_TP)) {
            sendCommandHelp(player, "arena tp", "<name>", "Teleport to the specified arena.");
        }
        if (player.hasPermission(PERMISSION_ARENA_SET_SPAWN)) {
            sendCommandHelp(player, "arena setspawn", "<team [red|blue]>", "Set the spawn location for the specified team.");
        }
        if (player.hasPermission(PERMISSION_ARENA_DELETE)) {
            sendCommandHelp(player, "arena delete", "<name>", "Delete the specified arena.");
        }
    }

    private void sendNoPerm(Player player) {
        player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
    }
}
