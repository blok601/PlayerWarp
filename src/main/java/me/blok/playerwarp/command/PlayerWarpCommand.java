package me.blok.playerwarp.command;

import me.blok.playerwarp.Core;
import me.blok.playerwarp.Messages;
import me.blok.playerwarp.Settings;
import me.blok.playerwarp.object.Warp;
import me.blok.playerwarp.object.handler.WarpHandler;
import me.blok.playerwarp.utils.ChatUtils;
import me.blok.playerwarp.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Blok on 8/9/2018.
 */
public class PlayerWarpCommand implements CommandExecutor {
    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do this command!");
            return false;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            // They did /pwarp - Open GUI
            WarpHandler.getInstance().openGUI(p);

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!p.hasPermission("playerwarp.create")) {
                    p.sendMessage(ChatUtils.format("&cNo permission!"));
                    return false;
                }
                String name = args[1];
                if (WarpHandler.getInstance().getWarp(name) != null) {
                    p.sendMessage(ChatUtils.message(Messages.getInstance().get("warp-taken")));
                    return false;
                }

                ItemStack skull;
                if(Core.getInstance().USE_NATIVE_VERSION){
                    //1.13
                    skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
                }else {
                    skull = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
                }
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwner(p.getName());
                skull.setItemMeta(skullMeta);

                ItemBuilder itemBuilder = new ItemBuilder(skull);
                itemBuilder.lore(ChatUtils.format("&5Votes&8: &e0"), true);

                Warp warp = new Warp(name);
                warp.setCreator(p.getUniqueId());
                warp.setItemStack(itemBuilder.make());
                warp.setLocation(p.getLocation());
                warp.setVotes(0);
                WarpHandler.getInstance().addWarp(warp);
                p.sendMessage(ChatUtils.message(Messages.getInstance().translate(Messages.getInstance().get("created-warp"), warp)));
                return true;
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                if (!p.hasPermission("playerwarp.remove")) {
                    p.sendMessage(ChatUtils.format("&cNo permission!"));
                    return false;
                }
                String name = args[1];
                if (WarpHandler.getInstance().getWarp(name) == null) {
                    p.sendMessage(ChatUtils.message(Messages.getInstance().get("invalid-warp")));
                    return false;
                }

                Warp warp = WarpHandler.getInstance().getWarp(name);
                WarpHandler.getInstance().removeWarp(warp);
                p.sendMessage(ChatUtils.message(Messages.getInstance().translate(Messages.getInstance().get("removed-warp"), warp)));
                return true;
            } else {
                sendHelp(p);
                return false;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!p.hasPermission("playerwarp.reload")) {
                    p.sendMessage(ChatUtils.format("&cNo permission!"));
                    return false;
                }

                WarpHandler.getInstance().save();
                Settings.getInstance().reloadMessages();
                Messages.getInstance().getMessages().clear();
                Messages.getInstance().setup();
                WarpHandler.getInstance().reload();
                p.sendMessage(ChatUtils.message(Messages.getInstance().get("reload")));
            } else {
                sendHelp(p);
                return false;
            }
        } else {
            sendHelp(p);
            return false;
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatUtils.message("&eHelp:"));
        if (player.hasPermission("playerwarp.create")) {
            player.sendMessage(ChatUtils.format("&e/playerwarp <create> <name> - Create's a warp with a given name and item in hand at your location"));
        }

        if (player.hasPermission("playerwarp.remove")) {
            player.sendMessage(ChatUtils.format("&e/playerwarp <remove> <name> - Remove a warp with the given name"));
        }

        if (player.hasPermission("playerwarp.warp")) {
            player.sendMessage(ChatUtils.format("&e/playerwarp - Opens a GUI with all of the player warps"));
        }

        if (player.hasPermission("playerwarp.reload")) {
            player.sendMessage(ChatUtils.format("&e/playerwarp reload - Reloads all messages and player warps"));
        }
    }
}
