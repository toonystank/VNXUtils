package com.toonystank.vnxutils.whitelist;

import com.toonystank.vnxutils.PlayerManager;
import com.toonystank.vnxutils.VNXUtils;
import com.toonystank.vnxutils.VnxPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WhitelistManager implements Listener {

    private Set<VnxPlayer> whitelistedPlayers = new HashSet<>();
    private WhitelistConfig whitelistConfig;

    public WhitelistManager(VNXUtils plugin) throws IOException {
        this.whitelistConfig = new WhitelistConfig(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        firstLoad();
    }
    public void firstLoad() throws IOException {
        if (whitelistConfig.getBoolean("firstload")) return;
        for (VnxPlayer value : PlayerManager.getPlayers()) {
            addWhitelistedPlayer(value);
        }
        whitelistConfig.set("firstload", true);

    }
    public void addWhitelistedPlayer(VnxPlayer player) throws IOException {
        whitelistedPlayers.add(player);
        whitelistConfig.addWhitelistedPlayer(player);
    }
    public void removeWhitelistedPlayer(VnxPlayer player) throws IOException {
        whitelistedPlayers.remove(player);
        whitelistConfig.removeWhitelistedPlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        VnxPlayer player = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
        if (!whitelistedPlayers.contains(player)) {
            event.getPlayer().kickPlayer("You are not whitelisted on this server. To get whitelisted, please join Taggernations discord server.");
        }
    }
}
