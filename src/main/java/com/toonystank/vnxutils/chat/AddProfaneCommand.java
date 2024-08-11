package com.toonystank.vnxutils.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.VNXUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandAlias("vnxutils")
public class AddProfaneCommand extends BaseCommand {

    private final VNXUtils plugin;
    private final ChatModerationConfig chatModerationConfig;

    public AddProfaneCommand(VNXUtils plugin) throws IOException {
        this.plugin = plugin;
        this.chatModerationConfig = new ChatModerationConfig(plugin);
    }
    @Subcommand("addprofane")
    @CommandAlias("addprofane")
    @Syntax("/addprofane <word>")
    @Description("Add a word to the list of profane words.")
    @CommandPermission("vnxutils.addprofane")
    public void onAddProfane(CommandSender sender, @Name("word") String word) {
        try {
            chatModerationConfig.addProfaneWord(word);
            sender.sendMessage("The word '" + word + "' has been added to the list of profane words.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subcommand("reloadconfig")
    @CommandAlias("reloadconfig")
    @Description("Reload the configuration file.")
    @CommandPermission("vnxutils.reloadconfig")
    public void onReloadConfig(CommandSender sender) {
        try {
            VNXUtils.staticInstance.mainConfig.reload();
            VNXUtils.staticInstance.mobDropManager.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Configuration file reloaded.");
    }

    @Subcommand("villagerlog")
    @CommandAlias("villagerlog")
    @Description("Log the death of a villager.")
    @CommandPermission("vnxutils.villagerlog")
    public void onVillagerLog(Player sender,int radius) {
        plugin.entityLogManager.getVillagersInRadius(sender,radius);
    }

    @Subcommand("spawn")
    @CommandAlias("spawn")
    @Description("Teleport to spawn.")
    @CommandPermission("vnxutils.spawn")
    public void onSpawn(Player sender) {
        try {
            plugin.teleportManager.teleportToSpawn(sender);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("setspawn")
    @CommandAlias("setspawn")
    @Description("Set the spawn location.")
    @CommandPermission("vnxutils.setspawn")
    public void onSetSpawn(Player sender) {
        try {
            plugin.teleportManager.setSpawnLocation(sender.getLocation());
            sender.sendMessage("Spawn location set.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("back")
    @CommandAlias("back")
    @Description("Teleport to the last location.")
    @CommandPermission("vnxutils.back")
    public void onBack(Player sender) {
        plugin.teleportManager.teleportToLastLocation(sender);
    }

}