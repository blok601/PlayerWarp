package me.blok.playerwarp;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Blok on 8/9/2018.
 */
public class Settings {
    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }


    FileConfiguration messages;
    File mFile;

    public void createFiles(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        mFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!mFile.exists()) {
            Bukkit.getServer().getLogger().info("[PlayerWarp] Messages.yml not found... creating!");
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getServer().getLogger().severe("[PlayerWarp] Could not create the messages.yml file!");
            }
        }

        messages = YamlConfiguration.loadConfiguration(mFile);
        messages.options().copyDefaults(true);
        saveMessage();
    }

    public void saveMessage() {
        try {
            messages.save(mFile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getServer().getLogger().severe("[PlayerWarp] Error saving messages.yml!");
        }
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(mFile);
    }

    public FileConfiguration getMessages(){
        return messages;
    }
}
