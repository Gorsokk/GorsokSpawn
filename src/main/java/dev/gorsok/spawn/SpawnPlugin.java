package dev.gorsok.spawn;

import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GorsokSpawn: /spawn with a warmup, a cooldown and editable messages.
 */
public final class SpawnPlugin extends JavaPlugin {

    private Messages messages;
    private TeleportManager teleports;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new Messages(this);
        teleports = new TeleportManager(this);

        getServer().getPluginManager().registerEvents(teleports, this);

        SpawnCommands commands = new SpawnCommands(this);
        register("spawn", commands);
        register("setspawn", commands);
        register("gspawn", commands);

        getLogger().info("GorsokSpawn enabled.");
    }

    @Override
    public void onDisable() {
        if (teleports != null) {
            teleports.cancelAll();
        }
    }

    private void register(String name, SpawnCommands commands) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().severe("Command /" + name + " is missing from plugin.yml");
            return;
        }
        command.setExecutor(commands);
        command.setTabCompleter(commands);
    }

    /** Reloads config.yml and the messages. */
    public void reload() {
        reloadConfig();
        messages.reload();
    }

    /** The saved spawn, or null if none is set or its world is not loaded. */
    public Location getSpawn() {
        Location location = getConfig().getLocation("spawn");
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return location;
    }

    public void setSpawn(Location location) {
        getConfig().set("spawn", location);
        saveConfig();
    }

    public Messages messages() {
        return messages;
    }

    public TeleportManager teleports() {
        return teleports;
    }
}
