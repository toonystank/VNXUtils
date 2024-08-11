package com.toonystank.vnxutils.drops;

import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MobDropManager implements Listener {

    private final MobDropConfig mobDropConfig;
    public Map<MobDrop,EnderDragon> enderDragons = new HashMap<>();

    public MobDropManager(VNXUtils plugin) throws IOException {
        this.mobDropConfig = new MobDropConfig(plugin);
    }
    public void reload() {
        try {
            mobDropConfig.reload();
            mobDropConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        for (MobDrop mobDrop : mobDropConfig.getMobDrops()) {
            if (mobDrop.mob() != event.getEntityType()) {
                continue;
            }
            if (dropItem(event.getEntity(),mobDrop)) {
                return;
            }
        }
    }
    public boolean dropItem(Entity entity,MobDrop mobDrop){
        if (Math.random() * 100 >= mobDrop.dropChance()) {
            MessageUtils.toConsole("Drop chance of " + mobDrop.dropChance() + "% not met");
            return false;
        }
        for (Material material : mobDrop.dropMaterial()) {
            MessageUtils.toConsole("Dropping " + material + " x" + mobDrop.dropAmount() + " at " + entity.getLocation());
            entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(material, mobDrop.dropAmount()));
        }
        return true;
    }
}
