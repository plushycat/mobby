package com.plushycat.mobby;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles the /mobby reload command in a separate class.
 */
public class ReloadCommand implements CommandExecutor {
    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mobby.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Mobby configuration reloaded.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /mobby reload");
        return true;
    }
}
