package com.plushycat.mobby;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import java.util.Map;


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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("mobby.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                    return true;
                }

                plugin.reloadPlugin();
                sender.sendMessage(ChatColor.GREEN + "Mobby configuration reloaded.");
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                    return true;
                }
                Player player = (Player) sender;
                if (!player.hasPermission("mobby.list")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                    return true;
                }

                var records = plugin.getRecordsFor(player.getUniqueId());
                if (records.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "You have no tracked mobs.");
                    return true;
                }

                player.sendMessage(ChatColor.GREEN + "Tracked mobs:");
                for (var entry : records.entrySet()) {
                    MobRecord r = entry.getValue();
                    String status = r.isAgeLocked() ? "mini-fied (age locked)" : "freed (ageing)";

            // build hover text (include location)
            String hover = "Type: " + r.getTypeName()
                + "\nUUID: " + r.getEntityId()
                + "\nStatus: " + status
                + "\nLocation: " + r.getWorld()
                + String.format(" (%.2f, %.2f, %.2f)", r.getX(), r.getY(), r.getZ());

                    TextComponent tc = new TextComponent(r.getDisplayName() + " - " + status);
                    tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
                    player.spigot().sendMessage(tc);
                }

                return true;
            }
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /mobby <reload|list>");
        return true;
    }
}
