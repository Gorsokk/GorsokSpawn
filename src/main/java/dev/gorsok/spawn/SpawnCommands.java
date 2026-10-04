package dev.gorsok.spawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * /spawn, /setspawn and /gspawn reload.
 */
public final class SpawnCommands implements CommandExecutor, TabCompleter {

    private final SpawnPlugin plugin;

    public SpawnCommands(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        Messages messages = plugin.messages();
        switch (command.getName().toLowerCase()) {
            case "spawn" -> {
                if (!(sender instanceof Player player)) {
                    messages.send(sender, "players-only");
                    return true;
                }
                plugin.teleports().requestSpawn(player);
            }
            case "setspawn" -> {
                if (!(sender instanceof Player player)) {
                    messages.send(sender, "players-only");
                    return true;
                }
                plugin.setSpawn(player.getLocation());
                messages.send(player, "spawn-set");
            }
            case "gspawn" -> {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    plugin.reload();
                    messages.send(sender, "reloaded");
                } else {
                    messages.send(sender, "usage-admin");
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("gspawn") && args.length == 1
                && "reload".startsWith(args[0].toLowerCase())) {
            return List.of("reload");
        }
        return List.of();
    }
}
