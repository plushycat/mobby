package com.plushycat.mobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Plugin extends JavaPlugin {
    FileConfiguration config;

    private AnimalListener animalListener;
    
    // player UUID -> (entity UUID -> MobRecord)
    private final Map<UUID, Map<UUID, MobRecord>> playerMobRecords = new ConcurrentHashMap<>();

    // periodic updater task
    private BukkitTask updaterTask;

    @Override
    public void onEnable() {
        // Copy packaged default config.yml to the plugin folder if it's missing.
        saveDefaultConfig();
        config = getConfig();

        // ensure plugin data folder exists
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        // load persisted mob records if present
        loadRecords();

        animalListener = new AnimalListener(this);
        getServer().getPluginManager().registerEvents(animalListener, this);

        // register the reload command executor
        if (getCommand("mobby") != null) {
            getCommand("mobby").setExecutor(new ReloadCommand(this));
        }

        // schedule periodic location updates
        int intervalSeconds = config.getInt("tracking.update-interval-seconds", 60);
        long ticks = Math.max(1L, intervalSeconds) * 20L;
        updaterTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updateLocations();
                } catch (Throwable t) {
                    getLogger().severe("Error while updating mob locations: " + t.getMessage());
                }
            }
        }.runTaskTimer(this, ticks, ticks);
    }

    

    /**
     * Reloads the plugin configuration and reinitializes the listener.
     */
    public void reloadPlugin() {
        reloadConfig();
        config = getConfig();

        if (animalListener != null) {
            HandlerList.unregisterAll(animalListener);
        }

        animalListener = new AnimalListener(this);
        getServer().getPluginManager().registerEvents(animalListener, this);
        // reschedule updater with new interval
        if (updaterTask != null) updaterTask.cancel();
        int intervalSeconds = config.getInt("tracking.update-interval-seconds", 60);
        long ticks = Math.max(1L, intervalSeconds) * 20L;
        updaterTask = new BukkitRunnable() {
            @Override
            public void run() {
                try { updateLocations(); } catch (Throwable t) { getLogger().severe("Error while updating mob locations: " + t.getMessage()); }
            }
        }.runTaskTimer(this, ticks, ticks);
    }

    

    private File getDataFile() {
        return new File(getDataFolder(), "data.yml");
    }

    public synchronized void saveRecords() {
        try {
            File dataFile = getDataFile();
            FileConfiguration data = new YamlConfiguration();

            for (Map.Entry<UUID, Map<UUID, MobRecord>> pEntry : playerMobRecords.entrySet()) {
                String pKey = pEntry.getKey().toString();
                for (Map.Entry<UUID, MobRecord> eEntry : pEntry.getValue().entrySet()) {
                    String eKey = eEntry.getKey().toString();
                    MobRecord r = eEntry.getValue();
                    String path = "records." + pKey + "." + eKey;
                    data.set(path + ".displayName", r.getDisplayName());
                    data.set(path + ".typeName", r.getTypeName());
                    data.set(path + ".ageLocked", r.isAgeLocked());
                    data.set(path + ".world", r.getWorld());
                    data.set(path + ".x", r.getX());
                    data.set(path + ".y", r.getY());
                    data.set(path + ".z", r.getZ());
                }
            }

            data.save(dataFile);
        } catch (IOException ex) {
            getLogger().severe("Failed to save mob records: " + ex.getMessage());
        }
    }

    public synchronized void loadRecords() {
        File dataFile = getDataFile();
        if (!dataFile.exists()) return;
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        if (!data.isConfigurationSection("records")) return;

        for (String playerKey : data.getConfigurationSection("records").getKeys(false)) {
            UUID playerUuid;
            try { playerUuid = UUID.fromString(playerKey); } catch (IllegalArgumentException ex) { continue; }
            Map<UUID, MobRecord> map = new ConcurrentHashMap<>();
            var playerSection = data.getConfigurationSection("records." + playerKey);
            if (playerSection == null) continue;
            for (String entityKey : playerSection.getKeys(false)) {
                try {
                    UUID entityUuid = UUID.fromString(entityKey);
                    var sec = playerSection.getConfigurationSection(entityKey);
                    if (sec == null) continue;
                    String display = sec.getString("displayName", "");
                    String typeName = sec.getString("typeName", "");
                    boolean ageLocked = sec.getBoolean("ageLocked", false);
                    String world = sec.getString("world", "");
                    double x = sec.getDouble("x", 0.0);
                    double y = sec.getDouble("y", 0.0);
                    double z = sec.getDouble("z", 0.0);
                    map.put(entityUuid, new MobRecord(entityUuid, display, typeName, ageLocked, world, x, y, z));
                } catch (IllegalArgumentException ex) {
                    // skip invalid UUIDs
                }
            }
            playerMobRecords.put(playerUuid, map);
        }
    }

    /** Register that a player mini-fied an entity (age lock applied). */
    public void registerMini(UUID player, java.util.UUID entityId, String displayName, String typeName, String world, double x, double y, double z) {
        playerMobRecords.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(entityId, new MobRecord(entityId, displayName, typeName, true, world, x, y, z));
        saveRecords();
    }

    /** Register that a player freed an entity (age lock removed). */
    public void registerFree(UUID player, java.util.UUID entityId, String displayName, String typeName, String world, double x, double y, double z) {
        playerMobRecords.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(entityId, new MobRecord(entityId, displayName, typeName, false, world, x, y, z));
        saveRecords();
    }

    /** Get records for a player. */
    public Map<UUID, MobRecord> getRecordsFor(UUID player) {
        return playerMobRecords.getOrDefault(player, Map.of());
    }

    private void updateLocations() {
        for (Map<UUID, MobRecord> map : playerMobRecords.values()) {
            for (Map.Entry<UUID, MobRecord> e : map.entrySet()) {
                UUID entityId = e.getKey();
                MobRecord r = e.getValue();
                Entity ent = Bukkit.getEntity(entityId);
                if (ent != null && ent.isValid()) {
                    var loc = ent.getLocation();
                    r.setLocation(ent.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
                }
            }
        }
        // persist after bulk update
        saveRecords();
    }

    @Override
    public void onDisable() {
        if (updaterTask != null) updaterTask.cancel();
        // persist records on shutdown
        saveRecords();
        super.onDisable();
    }
}
