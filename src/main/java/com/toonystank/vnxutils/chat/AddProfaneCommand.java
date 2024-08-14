package com.toonystank.vnxutils.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.toonystank.vnxutils.MessageUtils;
import com.toonystank.vnxutils.PlayerManager;
import com.toonystank.vnxutils.VNXUtils;
import com.toonystank.vnxutils.VnxPlayer;
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
            VnxPlayer player = PlayerManager.getPlayer(sender);
            plugin.teleportManager.teleportToSpawn(player);
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
        VnxPlayer player = PlayerManager.getPlayer(sender.getUniqueId());
        try {
            plugin.teleportManager.teleportToLastLocation(player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subcommand("isinspawnmode")
    @CommandAlias("isinspawnmode")
    @Description("Check if player did /spawn and did not do /back")
    @CommandPermission("vnxutils.spawnstatus")
    public void onSpawnStatus(Player player, String otherPlayer) {
        VnxPlayer vnxPlayer = PlayerManager.getPlayer(otherPlayer);
        if (plugin.teleportManager.getPlayerPlayerLocationMap().containsKey(vnxPlayer)) {
            player.sendMessage(otherPlayer + " is in spawn mode.");
        } else {
            player.sendMessage(otherPlayer + " is not in spawn mode.");
        }
    }
    @Subcommand("setlastlocation")
    @CommandAlias("setlastlocation")
    @Description("Set the last location.")
    @CommandPermission("vnxutils.setlastlocation")
    public void onSetLastLocation(Player sender) {
        try {
            VnxPlayer player = PlayerManager.getPlayer(sender);
            plugin.teleportManager.setLastLocation(player, sender.getLocation());
            sender.sendMessage("Last location set.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subcommand("removespawnmode")
    @CommandAlias("removespawnmode")
    @Description("Remove player from spawn mode.")
    @CommandPermission("vnxutils.removespawnmode")
    public void onRemoveSpawnMode(Player player, String otherPlayer) {
        VnxPlayer vnxPlayer = PlayerManager.getPlayer(otherPlayer);
        try {
            plugin.teleportManager.removeLastLocation(vnxPlayer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(otherPlayer + " removed from spawn mode.");
    }

}