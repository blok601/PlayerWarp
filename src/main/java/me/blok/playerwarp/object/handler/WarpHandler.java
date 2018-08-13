package me.blok.playerwarp.object.handler;

import me.blok.playerwarp.Core;
import me.blok.playerwarp.object.Warp;
import me.blok.playerwarp.utils.ChatUtils;
import me.blok.playerwarp.utils.ItemBuilder;
import me.blok.playerwarp.utils.PagedInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Blok on 8/9/2018.
 */
@SuppressWarnings("Duplicates")
public class WarpHandler {
    private static WarpHandler ourInstance = new WarpHandler();

    public static WarpHandler getInstance() {
        return ourInstance;
    }

    private WarpHandler() {
    }

    private ArrayList<Warp> warps;
    private FileConfiguration configuration;
    private HashMap<UUID, Long> lastVote = new HashMap<>();

    /*
    warpName:
      creator: creatorUUID
      item: serialized itemstack
      location: serialized location
      votes: vote amount
     */

    public void setup() {
        configuration = Core.getInstance().getConfig();
        this.warps = new ArrayList<>();
        ConfigurationSection section;
        for (String key : configuration.getKeys(false)) {
            if (key.equalsIgnoreCase("prefix")) continue; //Prefix not a warp!
            if(key.equalsIgnoreCase("vote")) continue;
            section = configuration.getConfigurationSection(key);
            Warp warp = new Warp(key);

            if (!section.contains("creator")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid creator! Skipping!");
                continue;
            }

            warp.setCreator(UUID.fromString(section.getString("creator")));

            if (!section.contains("item")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid item! Skipping!");
                continue;
            }

            warp.setItemStack(section.getItemStack("item"));

            if (!section.contains("location")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid location! Skipping!");
                continue;
            }

            Location location = (Location) section.get("location");
            if (location == null) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid location! Skipping!");
                continue;
            }

            if (!section.contains("votes")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have valid votes! Skipping!");
                continue;
            }
            warp.setVotes(section.getInt("votes"));

            warp.setLocation(location);
            this.warps.add(warp);
        }

        loadVotes();
    }

    public void reload() {
        save();
        this.warps.clear();
        ConfigurationSection section;
        for (String key : configuration.getKeys(false)) {
            if (key.equalsIgnoreCase("prefix")) continue; //Prefix not a warp!
            section = configuration.getConfigurationSection(key);
            Warp warp = new Warp(key);

            if (!section.contains("creator")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid creator! Skipping!");
                continue;
            }

            warp.setCreator(UUID.fromString(section.getString("creator")));

            if (!section.contains("item")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid item! Skipping!");
                continue;
            }

            warp.setItemStack(section.getItemStack("item"));

            if (!section.contains("location")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid location! Skipping!");
                continue;
            }

            Location location = (Location) section.get("location");
            if (location == null) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have a valid location! Skipping!");
                continue;
            }

            if (!section.contains("votes")) {
                Bukkit.getLogger().info("[PlayerWarp] Warp: " + key + " doesn't have valid votes! Skipping!");
                continue;
            }
            warp.setVotes(section.getInt("votes"));

            warp.setLocation(location);
            this.warps.add(warp);
        }
        loadVotes();
    }

    public void save() {
        for (Warp warp : this.warps) {
            configuration.set(warp.getName() + ".creator", warp.getCreator().toString());
            configuration.set(warp.getName() + ".votes", warp.getVotes());
            configuration.set(warp.getName() + ".item", warp.getItemStack());
            configuration.set(warp.getName() + ".location", warp.getLocation());
        }

        for (Map.Entry<UUID, Long> entry : this.lastVote.entrySet()) {
            configuration.set("vote." + entry.getKey(), entry.getValue());
        }

        Core.getInstance().saveConfig();
    }

    public void loadVotes(){
        ConfigurationSection section = configuration.getConfigurationSection("vote");
        if(section == null) return;
        //Nothing to load anyway
        UUID temp;
        for (String key : section.getKeys(false)) { //Loop UUIDs
            temp = UUID.fromString(key);
            this.lastVote.put(temp, configuration.getLong("vote." + temp.toString()));
        }
    }

    public Warp getWarp(String name) {
        for (Warp warp : this.warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public void removeWarp(Warp warp) {
        configuration.set(warp.getName(), null);
        this.warps.remove(warp);
        Core.getInstance().saveConfig();
    }

    public void addWarp(Warp warp) {
        this.warps.add(warp);
    }

    public Warp getWarp(ItemStack itemStack) {
        for (Warp warp : this.warps) {
            if (warp.getItemStack().equals(itemStack)) {
                return warp;
            }
        }
        return null;
    }

    public void openGUI(Player player) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        ArrayList<Warp> sortedWarps = new ArrayList<>(this.warps);
        sortedWarps.sort(Comparator.comparing(Warp::getVotes).reversed());

        ItemBuilder itemBuilder;
        for (Warp warp : sortedWarps) {
            itemBuilder = new ItemBuilder(warp.getItemStack());
            itemBuilder.name(ChatUtils.format("&a" + warp.getName()));
            itemStacks.add(itemBuilder.make());
        }


        new PagedInventory(itemStacks, ChatColor.translateAlternateColorCodes('&', "&6Player Warps"), player);
    }

    public HashMap<UUID, Long> getLastVote() {
        return lastVote;
    }
}
