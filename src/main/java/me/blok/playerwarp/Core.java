package me.blok.playerwarp;

import me.blok.playerwarp.command.PlayerWarpCommand;
import me.blok.playerwarp.listener.PlayerListener;
import me.blok.playerwarp.object.handler.WarpHandler;
import me.blok.playerwarp.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

    private static Core instance;

    public boolean USE_NATIVE_VERSION;

    @Override
    public void onEnable() {
        instance = this;
        Settings.getInstance().createFiles(this);
        WarpHandler.getInstance().setup();
        Messages.getInstance().setup();
        getCommand("pwarp").setExecutor(new PlayerWarpCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if (Bukkit.getVersion().contains("1.8")) {
            USE_NATIVE_VERSION = false;
        } else {
            USE_NATIVE_VERSION = true;
        }
        TimeUtils.setCooldownMins(getConfig().getLong("vote-cooldown"));
    }

    @Override
    public void onDisable() {
        WarpHandler.getInstance().save();
        Settings.getInstance().saveMessage();
        saveConfig();

        instance = null;
    }

    public static Core getInstance() {
        return instance;
    }
}
