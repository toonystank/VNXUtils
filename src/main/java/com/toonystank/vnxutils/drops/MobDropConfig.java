package com.toonystank.vnxutils.drops;

import com.toonystank.vnxutils.ConfigManger;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MobDropConfig extends ConfigManger {

    private final List<MobDrop> mobDrops = new ArrayList<>();

    public MobDropConfig(VNXUtils vnxUtils) throws IOException {
        super(vnxUtils, "mobdrops.yml", false, true);
        addDefault();
        load();
    }

    public void addDefault() {
        getConfig().addDefault("mobdrops.ENDER_DRAGON.dropChance", 100.0);
        getConfig().addDefault("mobdrops.ENDER_DRAGON.dropAmount", 1);
        getConfig().addDefault("mobdrops.ENDER_DRAGON.dropMaterial", "ELYTRA");
    }

    public void load() throws IOException {
        Set<String> section = getConfigurationSection("mobdrops", false, true);
        for (String mob : section) {
            try {
                MessageUtils.toConsole("Loading mob: " + mob);
                EntityType entityType = EntityType.valueOf(mob);
                double dropChance = getConfig().getDouble("mobdrops." + mob + ".dropChance");
                int dropAmount = getConfig().getInt("mobdrops." + mob + ".dropAmount");
                List<Material> dropMaterial = getStringList("mobdrops." + mob + ".dropMaterial").stream().map(Material::matchMaterial).toList();
                mobDrops.add(new MobDrop(entityType, dropChance, dropAmount, dropMaterial));
            } catch (IllegalArgumentException e) {
                MessageUtils.toConsole("Invalid mob type: " + mob);
            }
        }
    }

    public List<MobDrop> getMobDrops() {
        return Collections.unmodifiableList(mobDrops);
    }
}
