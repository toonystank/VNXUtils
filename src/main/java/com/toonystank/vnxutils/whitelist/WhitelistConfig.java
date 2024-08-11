package com.toonystank.vnxutils.whitelist;

import com.toonystank.vnxutils.ConfigManger;
import com.toonystank.vnxutils.PlayerManager;
import com.toonystank.vnxutils.VNXUtils;
import com.toonystank.vnxutils.VnxPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhitelistConfig extends ConfigManger {

    private WhitelistManager whitelistManager;

    public WhitelistConfig(VNXUtils plugin,WhitelistManager whitelistManager) throws IOException {
        super(plugin, "whitelist.yml","data",false,false);
        this.whitelistManager = whitelistManager;
        load();
    }

    public void load() {
        getStringList("whitelist").forEach(uuid -> {
            UUID uuid1 = UUID.fromString(uuid);
            VnxPlayer player = PlayerManager.getPlayer(uuid1);
            if (player != null) {
                try {
                    whitelistManager.addWhitelistedPlayer(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void addWhitelistedPlayer(VnxPlayer player) throws IOException {
        List<String> whitelist = getStringList("whitelist");
        if (whitelist.contains(player.uuid().toString())) {
            return;
        }
        whitelist.add(player.uuid().toString());
        set("whitelist",whitelist);
    }
    public void removeWhitelistedPlayer(VnxPlayer player) throws IOException {
        List<String> whitelist = getStringList("whitelist");
        whitelist.remove(player.uuid().toString());
        set("whitelist",whitelist);
    }
}
