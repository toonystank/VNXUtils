package com.toonystank.vnxutils.log;

import com.toonystank.vnxutils.ConfigManger;
import com.toonystank.vnxutils.VNXUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EntityLogConfig extends ConfigManger {

    public Set<VillagerLog> villagerLogs = new HashSet<>();
    public int interactedVillagers = 0;


    public EntityLogConfig(VNXUtils plugin) throws IOException {
        super(plugin, "entitylog.yml", false, false);
        loadVillagers();
        loadInteractedVillagerNumber();
    }

    public void saveInteractedVillagerNumber(int num) throws IOException {
        set("interactedVillagers", num);
    }
    public void loadInteractedVillagerNumber() throws IOException {
        interactedVillagers = getInt("interactedVillagers");
    }

    public void saveVillager(Villager villager,boolean isDespawn) throws IOException {
        VillagerLog villagerLog = new VillagerLog(villager.getName(), villager.getLocation(), villager.getProfession().getKey().value(), villager.getType().getKey().value(),isDespawn);
        villagerLogs.add(villagerLog);
        set("villagers." + villager.getUniqueId() + ".name", villager.getName());
        set("villagers." + villager.getUniqueId() + ".location.x", villager.getLocation().getX());
        set("villagers." + villager.getUniqueId() + ".location.y", villager.getLocation().getY());
        set("villagers." + villager.getUniqueId() + ".location.z", villager.getLocation().getZ());
        set("villagers." + villager.getUniqueId() + ".location.world", villager.getLocation().getWorld().getName());
        set("villagers." + villager.getUniqueId() + ".profession", villager.getProfession().getKey().value());
        set("villagers." + villager.getUniqueId() + ".type", villager.getType().getKey().value());
        set("villagers." + villager.getUniqueId() + ".isDespawn", isDespawn);
    }

    public void loadVillagers() throws IOException {
        Set<String> section = getConfigurationSection("villagers", false, true);
        for (String villager : section) {
            try {
                String name = getString("villagers." + villager + ".name");
                double x = getDouble("villagers." + villager + ".location.x");
                double y = getDouble("villagers." + villager + ".location.y");
                double z = getDouble("villagers." + villager + ".location.z");
                String world = getString("villagers." + villager + ".location.world");
                World bukkitWorld = plugin.getServer().getWorld(world);
                Location location = new Location(bukkitWorld, x, y, z);
                String profession = getString("villagers." + villager + ".profession");
                String type = getString("villagers." + villager + ".type");
                boolean isDespawn = getBoolean("villagers." + villager + ".isDespawn");
                VillagerLog villagerLog = new VillagerLog(name, location, profession, type,isDespawn);
                villagerLogs.add(villagerLog);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public record VillagerLog(String name, Location location, String profession, String type,boolean isDespawn) {
    }
}
