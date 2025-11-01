package com.plushycat.mobby;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

public class AnimalListener implements Listener
{
    private Plugin plugin;

    private Material stopGrowthItem;
    private Material stopGrowthItemAfter;
    private int stopGrowthAmount;
    private Material resumeGrowthItem;
    private Material resumeGrowthItemAfter;
    private int resumeGrowthAmount;

    public AnimalListener(Plugin plugin)
    {
        this.plugin = plugin;

        stopGrowthItem = getMaterialFromConfig("items.stopGrowth.before");
        stopGrowthItemAfter = getMaterialFromConfig("items.stopGrowth.after");
        stopGrowthAmount = plugin.config.getInt("items.stopGrowth.amount", 1);
        resumeGrowthItem = getMaterialFromConfig("items.resumeGrowth.before");
        resumeGrowthItemAfter = getMaterialFromConfig("items.resumeGrowth.after");
        resumeGrowthAmount = plugin.config.getInt("items.resumeGrowth.amount", 1);
    }

    private Material getMaterialFromConfig(String node)
    {
        String name = plugin.config.getString(node);
        if (name == null) return null;
        return Material.matchMaterial(name);
    }

    public void reloadConfigValues()
    {
        stopGrowthItem = getMaterialFromConfig("items.stopGrowth.before");
        stopGrowthItemAfter = getMaterialFromConfig("items.stopGrowth.after");
        stopGrowthAmount = plugin.config.getInt("items.stopGrowth.amount", 1);
        resumeGrowthItem = getMaterialFromConfig("items.resumeGrowth.before");
        resumeGrowthItemAfter = getMaterialFromConfig("items.resumeGrowth.after");
        resumeGrowthAmount = plugin.config.getInt("items.resumeGrowth.amount", 1);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
    {
        final Entity clicked = event.getRightClicked();
        if (!(clicked instanceof Ageable)) return;
        
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Ageable entity = (Ageable) clicked;
        final Location location = entity.getLocation();

        if (!entity.isAdult())
        {
            if (
                player.hasPermission("mobby.growth.stop")
                && inventory.getItemInMainHand() != null
                && inventory.getItemInMainHand().getType() == stopGrowthItem
                && !((Breedable) entity).getAgeLock()
            ) {
                // consume configured amount from the stack and give the 'after' item(s)
                ItemStack main = inventory.getItemInMainHand();
                int available = main.getAmount();
                int toConsume = Math.min(stopGrowthAmount, Math.max(0, available));
                if (toConsume <= 0) return;

                // apply effect
                location.getWorld().spawnParticle(Particle.HEART, location, 20);
                ((Breedable) entity).setAgeLock(true);

                // track for the player
                String custom = entity.getCustomName();
                String typeName = entity.getType().name();
                String display = (custom != null && !custom.isEmpty()) ? custom : prettify(typeName);
                plugin.registerMini(player.getUniqueId(), entity.getUniqueId(), display, typeName, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());

                // reduce stack (or clear if consumed entirely)
                if (available > toConsume) {
                    main.setAmount(available - toConsume);
                    inventory.setItemInMainHand(main);
                } else {
                    inventory.setItemInMainHand(new ItemStack(Material.AIR));
                }

                // give after items if applicable
                if (stopGrowthItemAfter != null && stopGrowthItemAfter != Material.AIR) {
                    inventory.addItem(new ItemStack(stopGrowthItemAfter, toConsume));
                }

                event.setCancelled(true);
            }

            else if (
                player.hasPermission("mobby.growth.resume")
                && inventory.getItemInMainHand() != null
                && inventory.getItemInMainHand().getType() == resumeGrowthItem
                && ((Breedable) entity).getAgeLock()
            ) {
                ItemStack main = inventory.getItemInMainHand();
                int available = main.getAmount();
                int toConsume = Math.min(resumeGrowthAmount, Math.max(0, available));
                if (toConsume <= 0) return;

                location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 20);
                ((Breedable) entity).setAgeLock(false);

                // track freeing for the player
                String custom2 = entity.getCustomName();
                String typeName2 = entity.getType().name();
                String display2 = (custom2 != null && !custom2.isEmpty()) ? custom2 : prettify(typeName2);
                plugin.registerFree(player.getUniqueId(), entity.getUniqueId(), display2, typeName2, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());

                if (available > toConsume) {
                    main.setAmount(available - toConsume);
                    inventory.setItemInMainHand(main);
                } else {
                    inventory.setItemInMainHand(new ItemStack(Material.AIR));
                }

                if (resumeGrowthItemAfter != null && resumeGrowthItemAfter != Material.AIR) {
                    inventory.addItem(new ItemStack(resumeGrowthItemAfter, toConsume));
                }

                event.setCancelled(true);
            }
        }
    }

    private String prettify(String enumName) {
        if (enumName == null) return "";
        String lower = enumName.toLowerCase().replace('_', ' ');
        if (lower.isEmpty()) return "";
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
