package com.toonystank.vnxutils;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageUtils {

    public static void sendMessage(List<VnxPlayer> sender, String message) {
        if (sender.isEmpty()) return;

        Set<VnxPlayer> playersSentMessage = new HashSet<>(); // Create set to track sent players

        for (VnxPlayer regionsPlayer : sender) {
            if (regionsPlayer == null || regionsPlayer.getOnlinePlayer() == null || playersSentMessage.contains(regionsPlayer)) {
                continue; // Skip null players or players already sent message
            }

            sendMessage(regionsPlayer.getOnlinePlayer(), message);
            playersSentMessage.add(regionsPlayer); // Mark player as sent
        }
    }

    public static void sendMessage(VnxPlayer sender, String message) {
        if (!sender.getPlayer().isOnline()) return;
        sendMessage(sender.getOnlinePlayer(),message);
    }
    public static void sendMessage(List<VnxPlayer> sender, String message,boolean titleMessage) {
        if (sender.isEmpty()) return;

        Set<VnxPlayer> playersSentMessage = new HashSet<>(); // Create set to track sent players

        for (VnxPlayer regionsPlayer : sender) {
            if (regionsPlayer == null || regionsPlayer.getOnlinePlayer() == null || playersSentMessage.contains(regionsPlayer)) {
                continue; // Skip null players or players already sent message
            }

            sendMessage(regionsPlayer.getOnlinePlayer(), message, titleMessage);
            playersSentMessage.add(regionsPlayer); // Mark player as sent
        }
    }
    public static void sendMessage(VnxPlayer sender, String message,boolean titleMessage) {
        if (!sender.getPlayer().isOnline()) return;
        sendMessage(sender.getOnlinePlayer(),message,titleMessage);
    }
    public static void sendMessage(Player sender, String message,boolean titleMessage) {
        sendMessage((CommandSender) sender, message,titleMessage);
    }
    public static void sendMessage(CommandSender sender, String message,boolean titleMessage) {
        if (!titleMessage) {
            sendMessage(sender,message);
            return;
        }
        if (!(sender instanceof Player player)) {
            sendMessage(sender,message);
            return;
        }
        message = formatString(message);
        player.sendTitle(message,"",10,70,10);
    }
    public static void sendMessage(Player sender, String message) {
        sendMessage((CommandSender) sender, message);
    }
    public static void sendMessage(CommandSender sender, String message) {
        MessageUtils.toConsole(message  + "  sending to player " + sender );
        Component component = new MineDown(message).toComponent();
        component = component.decoration(TextDecoration.ITALIC, false);
        send(sender,component);
       // Whitelist.staticInstance.adventure().sender(sender).sendMessage(component);
    }
    public static void sendMessage(VnxPlayer sender, Component message) {
        if (!sender.getPlayer().isOnline()) return;
        sendMessage(sender.getOnlinePlayer(),message);
    }
    public static void sendMessage(Player sender, Component message) {
        sendMessage((CommandSender) sender, message);
    }
    public static void sendMessage(CommandSender sender, Component message) {
        message = message.decoration(TextDecoration.ITALIC, false);
        send(sender,message);
    }
    public static @NotNull Component format(String message) {
        Component component = new MineDown(message).toComponent();
        component = component.decoration(TextDecoration.ITALIC, false);
        return component;
    }
    public static String formatString(String message) {
        if (message == null) return "null";
        BaseComponent[] baseComponents = de.themoep.minedown.MineDown.parse(message);
        return TextComponent.toLegacyText(baseComponents);
    }
    public static BaseComponent[] formatString(String message,int i) {
        return de.themoep.minedown.MineDown.parse(message);
    }
    public static void toConsole(List<String> list, boolean string) {
        list.forEach(MessageUtils::toConsole);
    }
    public static void toConsole(List<Component> list) {
        list.forEach(MessageUtils::toConsole);
    }
    public static void toConsole(String message) {
        message = "[VNXUtils] " + message;
        Component component = new MineDown(message).toComponent();
        toConsole(component);
    }
    public static void toConsole(Component component) {
        component = component.decoration(TextDecoration.ITALIC,false);
        send(VNXUtils.staticInstance.getServer().getConsoleSender(), component);
    }
    public static void error(String message) {
        message = message + ". Server version: " + VNXUtils.staticInstance.getServer().getVersion() + ". Plugin version: " + VNXUtils.staticInstance.getDescription().getVersion() + ". Please report this error to the plugin developer.";
        message = "[VNXUtils] " + message;
        Component component = new MineDown(message).toComponent();
        error(component);
    }
    public static void error(Component component) {
        try {
            component = component.decoration(TextDecoration.ITALIC, false);
            component = component.color(TextColor.fromHexString("#CF203E"));
            send(VNXUtils.staticInstance.getServer().getConsoleSender(), component);
        } catch (NullPointerException ignored) {
            error("an error occurred while sending a message");
        }
    }
    public static void debug(String message) {
        message = message + ". Server version: " + VNXUtils.staticInstance.getServer().getVersion() + ". Plugin version: " + VNXUtils.staticInstance.getDescription().getVersion() + ". To stop receiving this messages please update your config.yml";
        Component component = new MineDown(message).toComponent();
        debug(component);
    }
    public static void debug(Component component) {
        try {
            component = component.decoration(TextDecoration.ITALIC, false);
            send(VNXUtils.staticInstance.getServer().getConsoleSender(), component);
        } catch (NullPointerException ignored) {
            error("an error occurred while sending a message");
        }
    }
    public static void warning(String message) {
        message = "[VNXUtils] " + message;
        Component component = new MineDown(message).toComponent();
        warning(component);
    }
    public static void warning(Component component) {
        component = component.decoration(TextDecoration.ITALIC,false);
        component = component.color(TextColor.fromHexString("#FFC107"));
        send(VNXUtils.staticInstance.getServer().getConsoleSender(), component);
    }
    public static String replaceGrayWithWhite(String inputString) {
        if (inputString.contains("&7")) inputString = inputString.replace("&7", "&f");
        return inputString;
    }
    private static void send(CommandSender sender, Component component) {
        Audience.audience(sender).sendMessage(component);
    }
}
