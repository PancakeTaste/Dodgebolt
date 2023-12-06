package me.pancaketaste.dodgebolt;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dodgebolt extends JavaPlugin {
    @Override
    public void onEnable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("dodgebolt")) {
                if (args.length == 0) {
                    sendHelp(player);
                    return true;
                }

                switch (args[0]) {
                    case "join":
                        if (args.length == 1) {
                            sendHelp(player, "join");
                            return true;
                        }
                        break;
                    case "leave":
                        // ...
                        break;
                    case "arena": {
                        if (args.length == 1) {
                            sendHelp(player, "arena");
                            return true;
                        }

                        switch (args[1]) {
                            case "create":
                                if (args.length == 2) {
                                    sendHelp(player, "create");
                                    return true;
                                }
                                // ...
                                break;
                            case "info":
                                if (args.length == 2) {
                                    sendHelp(player, "info");
                                    return true;
                                }
                                // ...
                                break;
                            case "list":
                                if (args.length == 2) {
                                    sendHelp(player, "list");
                                    return true;
                                }
                                // ...
                                break;
                            case "addspawn":
                                if (args.length == 2) {
                                    sendHelp(player, "addspawn");
                                    return true;
                                }
                                // ...
                                break;
                            case "removespawns":
                                if (args.length == 2) {
                                    sendHelp(player, "removespawns");
                                    return true;
                                }
                                // ...
                                break;
                            case "delete":
                                if (args.length == 2) {
                                    sendHelp(player, "delete");
                                    return true;
                                }
                                // ...
                                break;
                            default:
                                sendHelp(player, "arena");
                                break;
                        }
                        break;
                    }
                    default:
                        sendHelp(player);
                }
            }
        } else {
            sender.sendMessage("This command can only be executed by players.");
        }

        return false;
    }

    private void sendHelp(Player player, String... specificCommands) {
        if (specificCommands.length == 0) {
            player.sendMessage(ChatColor.WHITE + "Dodgebolt Help:");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt join" + ChatColor.WHITE + " <arena> - Join queue.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt leave" + ChatColor.WHITE + " - Leave queue.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena create" + ChatColor.WHITE + " <name> - Create an arena.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena info" + ChatColor.WHITE + " <name> - See information about an arena.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena list" + ChatColor.WHITE + " - List all arenas.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena addspawn" + ChatColor.WHITE + " <team> - Set a new spawn for a team.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena removespawns" + ChatColor.WHITE + " <team> - Reset a spawn locations of a team.");
            player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena delete" + ChatColor.WHITE + " <name> - Delete an arena.");
        } else {
            for (String command : specificCommands) {
                switch (command.toLowerCase()) {
                    case "join":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt join" + ChatColor.WHITE + " <arena> - Join queue.");
                        break;
                    case "leave":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt leave" + ChatColor.WHITE + " - Leave queue.");
                        break;
                    case "arena":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena create" + ChatColor.WHITE + " <name> - Create an arena.");
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena info" + ChatColor.WHITE + " <name> - See information about an arena.");
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena list" + ChatColor.WHITE + " - List all arenas.");
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena addspawn" + ChatColor.WHITE + " <team> - Set a new spawn for a team.");
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena removespawns" + ChatColor.WHITE + " <team> - Reset a spawn locations of a team.");
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena delete" + ChatColor.WHITE + " <name> - Delete an arena.");
                        break;
                    case "create":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena create" + ChatColor.WHITE + " <name> - Create an arena.");
                        break;
                    case "info":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena info" + ChatColor.WHITE + " <name> - See information about an arena.");
                        break;
                    case "list":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena list" + ChatColor.WHITE + " - List all arenas.");
                        break;
                    case "addspawn":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena addspawn" + ChatColor.WHITE + " <team> - Set a new spawn for a team.");
                        break;
                    case "removespawns":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena removespawns" + ChatColor.WHITE + " <team> - Reset a spawn locations of a team.");
                        break;
                    case "delete":
                        player.sendMessage(ChatColor.YELLOW + "/dodgebolt arena delete" + ChatColor.WHITE + " <name> - Delete an arena.");
                        break;
                }
            }
        }
    }
}
