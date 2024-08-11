package com.toonystank.vnxutils.drops;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.List;

public record MobDrop(EntityType mob, double dropChance, int dropAmount, List<Material> dropMaterial) {
}
