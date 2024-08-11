package com.toonystank.vnxutils.whitelist;

import com.toonystank.vnxutils.PlayerManager;
import com.toonystank.vnxutils.VnxPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class Command implements TabExecutor {

    private final WhitelistManager whitelistManager;

    public Command(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            return false;
        }
        if (strings[0].equalsIgnoreCase("add")) {
            if (strings.length < 2) {
                return false;
            }
            VnxPlayer player = PlayerManager.getPlayer(strings[1]);
            try {
                whitelistManager.addWhitelistedPlayer(player);
                commandSender.sendMessage("Player added to the whitelist.");
            } catch (IOException e) {
                commandSender.sendMessage("An error occurred while adding the player to the whitelist.");
            }
            return true;
        }
        if (strings[0].equalsIgnoreCase("remove")) {
            if (strings.length < 2) {
                return false;
            }
            VnxPlayer player = PlayerManager.getPlayer(strings[1]);
            try {
                whitelistManager.removeWhitelistedPlayer(player);
                commandSender.sendMessage("Player removed from the whitelist.");
            } catch (IOException e) {
                commandSender.sendMessage("An error occurred while removing the player from the whitelist.");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
