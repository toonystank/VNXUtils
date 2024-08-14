package com.toonystank.vnxutils;

import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.toonystank.vnxutils.chat.ChatManager;
import com.toonystank.vnxutils.chat.AddProfaneCommand;
import com.toonystank.vnxutils.drops.MobDropConfig;
import com.toonystank.vnxutils.drops.MobDropManager;
import com.toonystank.vnxutils.log.EntityLogManager;
import com.toonystank.vnxutils.spawn.TeleportManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public final class VNXUtils extends JavaPlugin {

    public static VNXUtils staticInstance;
    public MainConfig mainConfig;
    public MobDropManager mobDropManager;
    public EntityLogManager entityLogManager;
    public TeleportManager teleportManager;
    public RegionContainer regionContainer;

    @Override
    public void onEnable() {
        staticInstance = this;
        MessageUtils.debug("VNXUtils is loading...");
        try {
            mainConfig = new MainConfig(this);
            mainConfig.load();
            MessageUtils.debug("MainConfig loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PlayerManager playerManager = new PlayerManager(this);
            MessageUtils.debug("PlayerManager loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ChatManager chatManager = new ChatManager(this);
            MessageUtils.debug("ChatModeration loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            mobDropManager = new MobDropManager(this);
            this.getServer().getPluginManager().registerEvents(mobDropManager, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        try {
            teleportManager = new TeleportManager(this);
            MessageUtils.debug("TeleportManager loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        entityLogManager = new EntityLogManager(this);
        this.getServer().getPluginManager().registerEvents(entityLogManager, this);
        try {
            // Register commands
            PaperCommandManager manager = new PaperCommandManager(this);
            manager.enableUnstableAPI("help");
            MessageUtils.debug("CommandManager loaded");
            manager.registerCommand(new AddProfaneCommand(this));

            // Register event listeners
            getServer().getPluginManager().registerEvents(new ChatManager(this), this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getScheduler().cancelTasks(this);
    }
}
