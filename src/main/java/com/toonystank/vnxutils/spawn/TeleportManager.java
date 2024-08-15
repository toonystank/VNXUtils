package com.toonystank.vnxutils.spawn;

import com.toonystank.vnxutils.*;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class TeleportManager extends ConfigManger {

    private final VNXUtils vnxUtils;
    private Location spawnLocation;
    private final SpawnLeaveProtection spawnLeaveProtection;

    private final Map<VnxPlayer,PlayerLocation> playerPlayerLocationMap = new HashMap<>();

    public TeleportManager(VNXUtils plugin) throws IOException {
        super(plugin, "teleport.yml", false, false);
        this.vnxUtils = plugin;
        loadPlayerData();
        loadSpawn();
        spawnLeaveProtection = new SpawnLeaveProtection(plugin,this);
    }

    public void loadSpawn() {
        String world = getString("spawn.world");
        double x = getDouble("spawn.x");
        double y = getDouble("spawn.y");
        double z = getDouble("spawn.z");
        if (world == null) {
            MessageUtils.toConsole("No spawn location found");
            return;
        }
        World bukkitWorld = vnxUtils.getServer().getWorld(world);
        if (bukkitWorld == null) {
            MessageUtils.toConsole("Invalid world: " + world);
            return;
        }
        this.spawnLocation = new Location(vnxUtils.getServer().getWorld(world), x, y, z);
    }
    public void loadPlayerData() throws IOException {
        for (String key : getConfigurationSection("lastLocation", false, true)) {
            String world = getString("lastLocation." + key + ".world");
            double x = getDouble("lastLocation." + key + ".x");
            double y = getDouble("lastLocation." + key + ".y");
            double z = getDouble("lastLocation." + key + ".z");
            Location location = new Location(vnxUtils.getServer().getWorld(world), x, y, z);
            VnxPlayer player = PlayerManager.getPlayer(UUID.fromString(key));
            playerPlayerLocationMap.put(player, new PlayerLocation(location));
        }
    }

    public void saveSpawnLocation(String world, double x, double y, double z) throws IOException {
        set("spawn.world", world);
        set("spawn.x", x);
        set("spawn.y", y);
        set("spawn.z", z);
    }

    public void setSpawnLocation(Location location) throws IOException {
        this.spawnLocation = location;
        saveSpawnLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public void setLastLocation(VnxPlayer player, Location location) throws IOException {
        playerPlayerLocationMap.put(player, new PlayerLocation(location));
        set("lastLocation." + player.uuid() + ".world", location.getWorld().getName());
        set("lastLocation." + player.uuid() + ".x", location.getX());
        set("lastLocation." + player.uuid() + ".y", location.getY());
        set("lastLocation." + player.uuid() + ".z", location.getZ());
    }
    public void removeLastLocation(VnxPlayer player) throws IOException {
        playerPlayerLocationMap.remove(player);
        set("lastLocation." + player.uuid(), null);
    }

    public void teleportToLastLocation(VnxPlayer player) throws IOException {
        if (!playerPlayerLocationMap.containsKey(player)) {
            MessageUtils.sendMessage(player, VNXUtils.staticInstance.mainConfig.getPrefix() + VNXUtils.staticInstance.mainConfig.getNoLastLocation());
            return;
        }
        PlayerLocation playerLocation = playerPlayerLocationMap.get(player);
        if (playerLocation != null) {
            removeLastLocation(player);
            player.getOnlinePlayer().teleportAsync(playerLocation.location);
            spawnLeaveProtection.playEffects(player,playerLocation.location);
            MessageUtils.sendMessage(player, VNXUtils.staticInstance.mainConfig.getPrefix() + VNXUtils.staticInstance.mainConfig.getTeleportedToLastLocation());
        }
    }

    public void teleportToSpawn(VnxPlayer player) throws IOException {
        if (spawnLocation == null) {
            MessageUtils.sendMessage(player, VNXUtils.staticInstance.mainConfig.getPrefix() + "No spawn location set.");
            return;
        }
        if (playerPlayerLocationMap.containsKey(player)) {
            MessageUtils.sendMessage(player, VNXUtils.staticInstance.mainConfig.getPrefix() + "Your already at spawn. Use /back to return to your last location.");
            return;
        }
        setLastLocation(player, player.getOnlinePlayer().getLocation());
        player.getOnlinePlayer().teleportAsync(spawnLocation);
        spawnLeaveProtection.playEffects(player,spawnLocation);
        MessageUtils.sendMessage(player, VNXUtils.staticInstance.mainConfig.getPrefix() + VNXUtils.staticInstance.mainConfig.getTeleportedToSpawn());
    }
    public record PlayerLocation(Location location) {
    }

    public void reload() throws IOException {
        super.reload();
        loadSpawn();
        loadPlayerData();
    }

}
