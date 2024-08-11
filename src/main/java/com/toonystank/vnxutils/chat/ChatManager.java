package com.toonystank.vnxutils.chat;

import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatManager implements Listener, ChatRenderer {

    private final ChatModerationConfig chatModerationConfig;
    private final Map<UUID, Long> lastMessageTime;

    public ChatManager(VNXUtils plugin) throws IOException {
        this.chatModerationConfig = new ChatModerationConfig(plugin);
        this.lastMessageTime = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lastMessageTime.containsKey(playerUUID)) {
            long lastTime = lastMessageTime.get(playerUUID);
            if (currentTime - lastTime < 2000) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You must wait 2 seconds between messages."));
                player.sendActionBar(Component.text("You must wait 2 seconds between messages."));
                return;
            }
        }

        lastMessageTime.put(playerUUID, currentTime);
        event.renderer(this);
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience audience) {
        String stringMessage = PlainTextComponentSerializer.plainText().serialize(message);
        String[] words = stringMessage.split("\\s+");
        Set<String> profaneWords = chatModerationConfig.getProfaneWords();
        String placeholderFormat = "{{PROFANE_WORD_%d}}";
        for (int i = 0; i < words.length; i++) {
            if (profaneWords.contains(words[i].toLowerCase())) {
                MessageUtils.toConsole("Profane word detected: " + words[i] + " from " + player.getName());
                words[i] = String.format(placeholderFormat, i);
            }
        }
        String filteredMessage = String.join(" ", words);
        String chatFormat = VNXUtils.staticInstance.getMainConfig().getChatFormat();
        if (chatFormat != null) {
            if (chatFormat.contains("{player}")) {
                chatFormat = chatFormat.replace("{player}", player.getName());
            }
            if (chatFormat.contains("{message}")) {
                chatFormat = chatFormat.replace("{message}", filteredMessage);
            }
            String placeholderFormatted = PlaceholderAPI.setPlaceholders(player, chatFormat);
            Component formattedMessage = MessageUtils.format(placeholderFormatted);
            if (player.hasPermission("vnxutils.bypassprofanity")) {
                return formattedMessage;
            }
            for (int i = 0; i < words.length; i++) {
                if (words[i].matches("\\{\\{PROFANE_WORD_\\d+}}")) {
                    String placeholder = String.format(placeholderFormat, i);
                    String replacement = "*".repeat(placeholder.length() - 2);
                    TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                            .matchLiteral(placeholder)
                            .replacement(Component.text(replacement))
                            .build();
                    formattedMessage = formattedMessage.replaceText(replacementConfig);
                }
            }
            return formattedMessage;
        }
        return Component.text("[").append(sourceDisplayName).append(Component.text("] ")).append(Component.text(filteredMessage));
    }
}
