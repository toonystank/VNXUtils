package com.toonystank.vnxutils;

import lombok.Getter;

import java.io.IOException;

@Getter
public class MainConfig extends ConfigManger{

    private String chatFormat;
    private String prefix;
    private String teleportedToSpawn;
    private String teleportedToLastLocation;
    private String noLastLocation;
    private String notAllowedToLeaveSpawn;

    public MainConfig(VNXUtils plugin) throws IOException {
        super(plugin, "config.yml", true, true);
    }

    public void load() throws IOException {
        this.chatFormat = getString("chat_format");
        this.prefix = getString("prefix", "&7[&fVNX&7]&r ");
        this.teleportedToSpawn = getString("teleported_to_spawn", "&7You have been teleported to spawn. do &f/back&7 to return to your last location.");
        this.teleportedToLastLocation = getString("teleported_to_last_location", "&7You have been teleported to your last location.");
        this.noLastLocation = getString("no_last_location", "&7You do not have a last location to return to.");
        this.notAllowedToLeaveSpawn = getString("not_allowed_to_leave_spawn", "&7You cannot leave spawn after using /spawn. Use /back to return to your previous location.");
    }
    public void reload() throws IOException {
        super.reload();
        load();
    }
    public String getString(String path,String defaultValue) throws IOException {
        if (!super.getConfig().contains(path)) {
            super.set(path,defaultValue);
            return defaultValue;
        }else {
            return super.getString(path);
        }
    }
}

