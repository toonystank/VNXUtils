package com.toonystank.vnxutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends ConfigManger implements Listener {

    private final VNXUtils plugin;
    private static final Map<UUID, VnxPlayer> playerUuidMap = new ConcurrentHashMap<>();
    private static final Map<String, UUID> playerNameMap = new ConcurrentHashMap<>();
    public static PlayerManager staticInstance;

    public PlayerManager(VNXUtils plugin) throws IOException {
        super(plugin, "playerdata.yml", "data",false,false);
        this.plugin = plugin;
        staticInstance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        load();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        VnxPlayer player = new VnxPlayer(event.getPlayer());
        addPlayer(player, true);
    }

    public void firstLoad() {
        Arrays.stream(plugin.getServer().getOfflinePlayers()).forEach(offlinePlayer -> {
            VnxPlayer player = new VnxPlayer(offlinePlayer);
            addPlayer(player, false);
        });
    }
    public void load() {
        firstLoad();
    }

    public static boolean addPlayer(VnxPlayer offlinePlayer, boolean bypassCheck) {
        if (!bypassCheck && (playerUuidMap.containsKey(offlinePlayer.uuid()) || playerNameMap.containsKey(offlinePlayer.name().toLowerCase()))) {
            return false;
        }
        playerUuidMap.put(offlinePlayer.uuid(), offlinePlayer);
        String name = Optional.ofNullable(offlinePlayer.name())
                .orElseGet(() -> {
                    OfflinePlayer bukkitOfflinePlayer = Bukkit.getOfflinePlayer(offlinePlayer.uuid());
                    return Optional.ofNullable(bukkitOfflinePlayer.getName())
                            .orElse("someone-(" + offlinePlayer.uuid() + ")");
                });
        playerNameMap.put(name.toLowerCase(), offlinePlayer.uuid());
        return true;
    }

    public static @Nullable VnxPlayer getPlayer(String name) {
        if (name == null) return null;
        return getPlayerFromMaps(name.toLowerCase()).orElseGet(() -> {
            Player onlinePlayer = Bukkit.getPlayer(name);
            if (onlinePlayer != null) {
                VnxPlayer newPlayer = new VnxPlayer(onlinePlayer);
                addPlayer(newPlayer, true);
                return newPlayer;
            }
            return null;
        });
    }

    public static @Nullable VnxPlayer getPlayer(UUID uuid) {
        if (uuid == null) return null;
        return getPlayerFromMaps(uuid).orElseGet(() -> {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                VnxPlayer newPlayer = new VnxPlayer(onlinePlayer);
                addPlayer(newPlayer, true);
                return newPlayer;
            }
            return null;
        });
    }
    public static @NotNull VnxPlayer getPlayer(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (playerUuidMap.containsKey(player.getUniqueId())) {
            return playerUuidMap.get(player.getUniqueId());
        }else {
            VnxPlayer newPlayer = new VnxPlayer(player);
            addPlayer(newPlayer, true);
            return newPlayer;
        }
    }

    private static Optional<VnxPlayer> getPlayerFromMaps(String name) {
        return Optional.ofNullable(playerNameMap.get(name))
                .map(playerUuidMap::get);
    }

    private static Optional<VnxPlayer> getPlayerFromMaps(UUID uuid) {
        return Optional.ofNullable(playerUuidMap.get(uuid));
    }

    public static List<VnxPlayer> getPlayers() {
        return new ArrayList<>(playerUuidMap.values());
    }
}
