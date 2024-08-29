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
import java.util.regex.Pattern;

public class ChatManager implements Listener, ChatRenderer {

    private final ChatModerationConfig chatModerationConfig;
    private final Map<UUID, Long> lastMessageTime;
    private static final long MESSAGE_COOLDOWN = 2000;

    public ChatManager(VNXUtils plugin) throws IOException {
        this.chatModerationConfig = new ChatModerationConfig(plugin);
        this.lastMessageTime = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (isOnCooldown(playerUUID, currentTime)) {
            event.setCancelled(true);
            sendCooldownMessage(player);
            return;
        }

        lastMessageTime.put(playerUUID, currentTime);
        event.renderer(this);
    }

    private boolean isOnCooldown(UUID playerUUID, long currentTime) {
        return lastMessageTime.containsKey(playerUUID) && currentTime - lastMessageTime.get(playerUUID) < MESSAGE_COOLDOWN;
    }

    private void sendCooldownMessage(Player player) {
        Component cooldownMessage = Component.text("You must wait 2 seconds between messages.");
        player.sendMessage(cooldownMessage);
        player.sendActionBar(cooldownMessage);
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience audience) {
        String messageText = PlainTextComponentSerializer.plainText().serialize(message);
        Set<String> profaneWords = chatModerationConfig.getProfaneWords();

        String filteredMessage = filterProfanity(messageText, profaneWords);
        String chatFormat = VNXUtils.staticInstance.getMainConfig().getChatFormat();

        return formatMessage(player, sourceDisplayName, filteredMessage, chatFormat);
    }

    private String filterProfanity(String message, Set<String> profaneWords) {
        for (String profaneWord : profaneWords) {
            String regex = "\\b" + Pattern.quote(profaneWord) + "\\b";
            String replacement = "*".repeat(profaneWord.length());
            message = message.replaceAll("(?i)" + regex, replacement);
        }
        return message;
    }

    private Component formatMessage(Player player, Component sourceDisplayName, String filteredMessage, String chatFormat) {
        if (chatFormat != null) {
            chatFormat = replacePlaceholders(player, chatFormat, filteredMessage);
            Component formattedMessage = MessageUtils.format(chatFormat);

            if (!player.hasPermission("vnxutils.bypassprofanity")) {
                return formattedMessage;
            }
        }
        return Component.text("[").append(sourceDisplayName).append(Component.text("] ")).append(Component.text(filteredMessage));
    }

    private String replacePlaceholders(Player player, String chatFormat, String filteredMessage) {
        chatFormat = chatFormat.replace("{player}", player.getName())
                .replace("{message}", filteredMessage);
        return PlaceholderAPI.setPlaceholders(player, chatFormat);
    }
}
