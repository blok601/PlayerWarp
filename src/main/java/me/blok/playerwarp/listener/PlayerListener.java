package me.blok.playerwarp.listener;

import me.blok.playerwarp.Messages;
import me.blok.playerwarp.object.Warp;
import me.blok.playerwarp.object.handler.WarpHandler;
import me.blok.playerwarp.utils.ChatUtils;
import me.blok.playerwarp.utils.ItemBuilder;
import me.blok.playerwarp.utils.PagedInventory;
import me.blok.playerwarp.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Blok on 8/9/2018.
 */
public class PlayerListener implements Listener {

    public static HashMap<UUID, PagedInventory> users = new HashMap<>();

    public static HashMap<UUID, PagedInventory> getUsers() {
        return users;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        //Get the current scroller inventory the player is looking at, if the player is looking at one.
        if (!users.containsKey(p.getUniqueId())) return;

        if (event.getInventory().getName() == null) return;
        if (event.getInventory().getName().equalsIgnoreCase(ChatColor.stripColor(event.getInventory().getName())))
            return;

        PagedInventory inv = users.get(p.getUniqueId());

        if (!event.getInventory().getName().equalsIgnoreCase(inv.name)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        //If the pressed item was a nextpage button
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.nextPageName)) {

            //If there is no next page, don't do anything
            if (inv.currpage >= inv.pages.size() - 1) {
                return;
            } else {
                //Next page exists, flip the page
                inv.currpage += 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
            //if the pressed item was a previous page button
        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.previousPageName)) {
            //If the page number is more than 0 (So a previous page exists)
            if (inv.currpage > 0) {
                //Flip to previous page
                inv.currpage -= 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
        } else {
            event.setCancelled(true);
            ItemStack stack = event.getCurrentItem();
            //Code here
            if (stack == null) return;
            if (stack.getType() == Material.AIR) return;
            String name = stack.getItemMeta().getDisplayName();
            String raw = ChatColor.stripColor(name);
            Warp warp = WarpHandler.getInstance().getWarp(raw);
            if (warp == null) {
                p.sendMessage(ChatUtils.message(Messages.getInstance().get("invalid-warp")));
                return;
            }

            if (event.getClick() == ClickType.RIGHT) {
                if(WarpHandler.getInstance().getLastVote().containsKey(p.getUniqueId())){
                    //They have a last vote
                    if(TimeUtils.hasDayPassed(WarpHandler.getInstance().getLastVote().get(p.getUniqueId()))){
                        WarpHandler.getInstance().getLastVote().put(p.getUniqueId(), System.currentTimeMillis());
                    }else{
                        if(!p.hasPermission("pwarp.bypass")){
                            p.closeInventory();
                            p.sendMessage(ChatUtils.message(Messages.getInstance().get("invalid-vote")));
                            return;
                        }
                    }
                }
                warp.addVote();
                p.closeInventory();
                p.sendMessage(ChatUtils.message(Messages.getInstance().translate(Messages.getInstance().get("add-vote"), warp)));
                ItemBuilder builder = new ItemBuilder(stack);
                builder.removeLore(0);
                builder.lore(ChatUtils.format("&5Votes&8: &e" + warp.getVotes()), true);
                event.getInventory().setItem(event.getSlot(), builder.make());
                p.updateInventory();
                warp.setItemStack(builder.make());
            }


            if (event.getClick() == ClickType.LEFT) {
                p.closeInventory();
                p.teleport(warp.getLocation());
                p.sendMessage(ChatUtils.message(Messages.getInstance().translate(Messages.getInstance().get("warping"), warp)));
                return;
            }
        }

        if (!users.containsKey(p.getUniqueId())) {
            users.put(p.getUniqueId(), inv);
        }

    }
}
