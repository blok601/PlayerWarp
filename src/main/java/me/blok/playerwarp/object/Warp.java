package me.blok.playerwarp.object;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Blok on 8/9/2018.
 */
public class Warp {

    private UUID creator;
    private String name;
    private Location location;
    private ItemStack itemStack;
    private int votes;

    public Warp(String name) {
        this.name = name;
    }

    public UUID getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setCreator(UUID creator) {
        this.creator = creator;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote(){
        this.votes++;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
