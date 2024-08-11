package com.toonystank.vnxutils.spawn;

import com.toonystank.vnxutils.ConfigManger;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TeleportManager extends ConfigManger {

    private final VNXUtils vnxUtils;
    private Location spawnLocation;

    private final Map<Player,PlayerLocation> playerPlayerLocationMap = new HashMap<>();

    public TeleportManager(VNXUtils plugin) throws IOException {
        super(plugin, "teleport.yml", false, false);
        this.vnxUtils = plugin;
        loadPlayerData();
        loadSpawn();
        SpawnLeaveProtection spawnLeaveProtection = new SpawnLeaveProtection(plugin,this);
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
            playerPlayerLocationMap.put(vnxUtils.getServer().getPlayer(key), new PlayerLocation(location));
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

    public void setLastLocation(Player player, Location location) throws IOException {
        playerPlayerLocationMap.put(player, new PlayerLocation(location));
        set("lastLocation." + player.getUniqueId() + ".world", location.getWorld().getName());
        set("lastLocation." + player.getUniqueId() + ".x", location.getX());
        set("lastLocation." + player.getUniqueId() + ".y", location.getY());
        set("lastLocation." + player.getUniqueId() + ".z", location.getZ());
    }

    public void teleportToLastLocation(Player sender) {
        if (!playerPlayerLocationMap.containsKey(sender)) {
            MessageUtils.sendMessage(sender, VNXUtils.staticInstance.mainConfig.getPrefix() + VNXUtils.staticInstance.mainConfig.getNoLastLocation());
            return;
        }
        PlayerLocation playerLocation = playerPlayerLocationMap.get(sender);
        if (playerLocation != null) {
            sender.teleportAsync(playerLocation.location);
            MessageUtils.sendMessage(sender, VNXUtils.staticInstance.mainConfig.getPrefix() + VNXUtils.staticInstance.mainConfig.getTeleportedToLastLocation());
        }
        playerPlayerLocationMap.remove(sender);
    }

    public void teleportToSpawn(Player player) throws IOException {
        setLastLocation(player, player.getLocation());
        player.teleportAsync(spawnLocation);
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
