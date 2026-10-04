package dev.gorsok.spawn;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

/**
 * Reads messages from config.yml (MiniMessage format) and sends them.
 */
public final class Messages {

    private final SpawnPlugin plugin;
    private final MiniMessage mini = MiniMessage.miniMessage();
    private String prefix = "";

    public Messages(SpawnPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        prefix = plugin.getConfig().getString("messages.prefix", "");
    }

    /** Sends a chat message with the prefix. */
    public void send(Audience to, String key, TagResolver... placeholders) {
        to.sendMessage(render(prefix + raw(key), placeholders));
    }

    /** Shows a message above the hotbar (no prefix). */
    public void actionBar(Player to, String key, TagResolver... placeholders) {
        to.sendActionBar(render(raw(key), placeholders));
    }

    public static TagResolver seconds(long seconds) {
        return Placeholder.unparsed("seconds", Long.toString(seconds));
    }

    private String raw(String key) {
        return plugin.getConfig().getString("messages." + key, "<red>Missing message: " + key + "</red>");
    }

    private Component render(String text, TagResolver... placeholders) {
        return mini.deserialize(text, TagResolver.resolver(placeholders));
    }
}
