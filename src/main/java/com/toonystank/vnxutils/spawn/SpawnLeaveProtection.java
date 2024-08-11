package com.toonystank.vnxutils.spawn;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
    }

    private boolean loadSpawnRegion() {
        if (teleportManager.getSpawnLocation() == null) {
            return false;
        }
        World world = BukkitAdapter.adapt(teleportManager.getSpawnLocation().getWorld());
        RegionManager regionManager = plugin.getRegionContainer().get(world);
        if (regionManager == null) {
            return false;
        }
        spawnRegion = regionManager.getRegion("spawn");
        return spawnRegion != null;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (spawnRegion == null || teleportManager.getPlayerPlayerLocationMap().get(event.getPlayer()) == null) {
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
}
