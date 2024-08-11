package com.toonystank.vnxutils.log;

import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.*;

public class EntityLogManager implements Listener {

    private VNXUtils plugin;
    private EntityLogConfig entityLogConfig;
    private final Set<UUID> deadVillagers = new HashSet<>();


    public EntityLogManager(VNXUtils plugin) {
        this.plugin = plugin;
        try {
            entityLogConfig = new EntityLogConfig(plugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void reload() {
        try {
            entityLogConfig.reload();
            entityLogConfig.loadVillagers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) {
            return;
        }
        int numInteractedVillagers = entityLogConfig.interactedVillagers;
        entityLogConfig.interactedVillagers = numInteractedVillagers + 1;
        try {
            entityLogConfig.saveInteractedVillagerNumber(entityLogConfig.interactedVillagers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (villager.customName() != null) {
            return;
        }

        villager.customName(Component.text("Villager #" + entityLogConfig.interactedVillagers));

    }
    public void getVillagersInRadius(Player player, int radius){
        Map<Integer,Component> messages = new HashMap<>();
        int numVillagers = 0;
        for (EntityLogConfig.VillagerLog villagerLog : entityLogConfig.villagerLogs) {
            numVillagers++;
            if (player.getLocation().distance(villagerLog.location()) <= radius) {
                Component message = MessageUtils.format("&e" + numVillagers + " &fName: &e" + villagerLog.name() + " &fProfession: &e" + villagerLog.profession() + " &fType: &e" + villagerLog.type() + " &fLocation: &e" + villagerLog.location().getBlockX() + " " + villagerLog.location().getBlockY() + " " + villagerLog.location().getBlockZ() + " &fIs Despawned: &e" + villagerLog.isDespawn());
                messages.put(numVillagers,message);
            }
        }
        messages.keySet().stream().sorted().forEachOrdered(i -> player.sendMessage(messages.get(i)));

    }
}

