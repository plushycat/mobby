package com.plushycat.mobby;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

public class Plugin extends JavaPlugin
{
    FileConfiguration config;

    private AnimalListener animalListener;

    @Override
    public void onEnable()
    {
        // Save packaged default config to the plugin data folder if missing
        saveDefaultConfig();
        config = getConfig();

        animalListener = new AnimalListener(this);
        getServer().getPluginManager().registerEvents(animalListener, this);

        // register the reload command executor
        if (getCommand("mobby") != null) {
            getCommand("mobby").setExecutor(new ReloadCommand(this));
        }
    }

    @Override
    public void onDisable() {}

    /**
     * Reloads the plugin configuration and reinitializes the listener.
     */
    public void reloadPlugin()
    {
        reloadConfig();
        config = getConfig();

        if (animalListener != null) {
            HandlerList.unregisterAll(animalListener);
        }

        animalListener = new AnimalListener(this);
        getServer().getPluginManager().registerEvents(animalListener, this);
    }
}
