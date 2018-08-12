package me.blok.playerwarp.utils;

import me.blok.playerwarp.Core;
import org.bukkit.ChatColor;

/**
 * Created by Blok on 8/9/2018.
 */
public class ChatUtils {

    public static final String PREFIX = Core.getInstance().getConfig().getString("prefix");

    public static String message(String string){
        return format(PREFIX + string);
    }

    public static String format(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
