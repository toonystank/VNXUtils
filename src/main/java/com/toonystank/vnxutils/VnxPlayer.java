package com.toonystank.vnxutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record VnxPlayer(String name, UUID uuid) {


    public VnxPlayer(@NotNull OfflinePlayer offlinePlayer) {
        this(offlinePlayer.getName(), offlinePlayer.getUniqueId());
    }
    public VnxPlayer(@NotNull Player offlinePlayer) {
        this(offlinePlayer.getName(), offlinePlayer.getUniqueId());
    }
    public @NotNull OfflinePlayer getPlayer() {
        OfflinePlayer offlinePlayer = VNXUtils.staticInstance.getServer().getPlayer(uuid);
        if (offlinePlayer == null) {
            offlinePlayer = VNXUtils.staticInstance.getServer().getOfflinePlayer(uuid);
        }
        return offlinePlayer;
    }

    public @Nullable Player getOnlinePlayer() throws NullPointerException {
        Player player = VNXUtils.staticInstance.getServer().getPlayer(uuid);
        if (player == null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getUniqueId().equals(uuid)) player = onlinePlayer;
            }
        }
        return player;
    }

    public boolean isOnline() {
        if (getOnlinePlayer() == null) return false;
        return getOnlinePlayer().isOnline();
    }
}
