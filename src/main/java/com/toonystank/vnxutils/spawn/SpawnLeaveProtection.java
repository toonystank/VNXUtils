package com.toonystank.vnxutils.spawn;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.PlayerManager;
import com.toonystank.vnxutils.VNXUtils;
import com.toonystank.vnxutils.VnxPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class SpawnLeaveProtection implements Listener {

    private final VNXUtils plugin;
    private final TeleportManager teleportManager;
    private ProtectedRegion spawnRegion;

    public SpawnLeaveProtection(VNXUtils plugin, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;

        if (!loadSpawnRegion()) {
            MessageUtils.toConsole("Spawn region not found");
            return;
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        run();
    }

    private boolean loadSpawnRegion() {
        Location spawnLocation = teleportManager.getSpawnLocation();
        if (spawnLocation == null) {
            return false;
        }

        World world = BukkitAdapter.adapt(spawnLocation.getWorld());
        RegionManager regionManager = plugin.getRegionContainer().get(world);

        if (regionManager == null) {
            return false;
        }

        spawnRegion = regionManager.getRegion("spawn");
        return spawnRegion != null;
    }

    public void run() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            teleportManager.getPlayerPlayerLocationMap().forEach((player, playerLastLocation) -> {

                if (player.getOnlinePlayer() == null) return;
                Location playerLocation = player.getOnlinePlayer().getLocation();
                if (isOutsideSpawnRegion(playerLocation)) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> handlePlayerExit(player));
                }
            });
        }, 0, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        VnxPlayer player = PlayerManager.getPlayer(event.getPlayer());
        if (spawnRegion == null || teleportManager.getPlayerPlayerLocationMap().get(player) == null) {
            return;
        }

        if (isLeavingRegion(event)) {
            event.setCancelled(true);
            MessageUtils.sendMessage(event.getPlayer(),
                    plugin.getMainConfig().getPrefix() + plugin.getMainConfig().getNotAllowedToLeaveSpawn());
        }
    }

    private boolean isLeavingRegion(PlayerMoveEvent event) {
        return spawnRegion.contains((int) event.getFrom().getX(), (int) event.getFrom().getY(), (int) event.getFrom().getZ()) &&
                !spawnRegion.contains((int) event.getTo().getX(), (int) event.getTo().getY(), (int) event.getTo().getZ());
    }

    private boolean isOutsideSpawnRegion(Location playerLocation) {
        return !teleportManager.getSpawnLocation().getWorld().getUID().equals(playerLocation.getWorld().getUID()) ||
                !spawnRegion.contains((int) playerLocation.getX(), (int) playerLocation.getY(), (int) playerLocation.getZ());
    }

    private void handlePlayerExit(VnxPlayer player) {
        Entity vehicle = player.getOnlinePlayer().getVehicle();
        try {
            if (vehicle != null) {
                vehicle.eject();
            }

            teleportManager.setLastLocation(player, player.getOnlinePlayer().getLocation());
            player.getOnlinePlayer().teleportAsync(teleportManager.getSpawnLocation());
            playEffects(player, teleportManager.getSpawnLocation());

            MessageUtils.sendMessage(player,
                    plugin.getMainConfig().getPrefix() + plugin.getMainConfig().getNotAllowedToLeaveSpawn());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playEffects(VnxPlayer player, Location playerLocation) {
        player.getOnlinePlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        player.getOnlinePlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 1));
        player.getOnlinePlayer().playSound(playerLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 5, 2);
    }
}
