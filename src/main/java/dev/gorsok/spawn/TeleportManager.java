package dev.gorsok.spawn;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles warmups (wait before teleporting) and cooldowns (wait between uses).
 */
public final class TeleportManager implements Listener {

    private final SpawnPlugin plugin;
    private final Map<UUID, BukkitTask> warmups = new HashMap<>();
    private final Map<UUID, Long> lastUse = new HashMap<>();

    public TeleportManager(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    /** Starts the /spawn process for a player: checks, warmup, then teleport. */
    public void requestSpawn(Player player) {
        Messages messages = plugin.messages();
        Location spawn = plugin.getSpawn();
        if (spawn == null) {
            messages.send(player, "no-spawn");
            return;
        }
        if (warmups.containsKey(player.getUniqueId())) {
            messages.send(player, "already-waiting");
            return;
        }

        long remaining = cooldownRemaining(player);
        if (remaining > 0) {
            messages.send(player, "cooldown", Messages.seconds(remaining));
            return;
        }

        int warmup = Math.max(0, plugin.getConfig().getInt("warmup-seconds", 3));
        if (warmup == 0 || player.hasPermission("gspawn.bypass.warmup")) {
            teleport(player);
            return;
        }

        messages.send(player, "warmup-start", Messages.seconds(warmup));
        int[] secondsLeft = {warmup};
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (secondsLeft[0] <= 0) {
                cancelWarmup(player.getUniqueId());
                teleport(player);
                return;
            }
            messages.actionBar(player, "warmup-actionbar", Messages.seconds(secondsLeft[0]));
            secondsLeft[0]--;
        }, 0L, 20L);
        warmups.put(player.getUniqueId(), task);
    }

    private void teleport(Player player) {
        Location spawn = plugin.getSpawn();
        if (spawn == null) {
            plugin.messages().send(player, "no-spawn");
            return;
        }
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("effects.particles", true)) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 40, 0.4, 0.6, 0.4, 0.2);
        }
        player.teleportAsync(spawn).thenAccept(success -> {
            if (!success) {
                return;
            }
            lastUse.put(player.getUniqueId(), System.currentTimeMillis());
            if (config.getBoolean("effects.sound", true)) {
                player.playSound(spawn, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }
            if (config.getBoolean("effects.particles", true)) {
                spawn.getWorld().spawnParticle(Particle.PORTAL, spawn.clone().add(0, 1, 0), 40, 0.4, 0.6, 0.4, 0.2);
            }
            plugin.messages().send(player, "teleported");
        });
    }

    /** Seconds left before the player can use /spawn again (0 = ready). */
    private long cooldownRemaining(Player player) {
        if (player.hasPermission("gspawn.bypass.cooldown")) {
            return 0;
        }
        long cooldownMs = Math.max(0, plugin.getConfig().getLong("cooldown-seconds", 30)) * 1000L;
        Long last = lastUse.get(player.getUniqueId());
        if (last == null || cooldownMs == 0) {
            return 0;
        }
        long leftMs = last + cooldownMs - System.currentTimeMillis();
        return leftMs <= 0 ? 0 : (leftMs + 999) / 1000;
    }

    private boolean cancelWarmup(UUID id) {
        BukkitTask task = warmups.remove(id);
        if (task == null) {
            return false;
        }
        task.cancel();
        return true;
    }

    public void cancelAll() {
        warmups.values().forEach(BukkitTask::cancel);
        warmups.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("cancel-on-move", true)) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        boolean changedBlock = from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
        if (changedBlock && cancelWarmup(event.getPlayer().getUniqueId())) {
            plugin.messages().send(event.getPlayer(), "cancelled-move");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!plugin.getConfig().getBoolean("cancel-on-damage", true)) {
            return;
        }
        if (event.getEntity() instanceof Player player && cancelWarmup(player.getUniqueId())) {
            plugin.messages().send(player, "cancelled-damage");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancelWarmup(event.getPlayer().getUniqueId());
    }
}
