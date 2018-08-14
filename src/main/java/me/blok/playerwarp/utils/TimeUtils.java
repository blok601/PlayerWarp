package me.blok.playerwarp.utils;

import me.blok.playerwarp.object.handler.WarpHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TimeUtils {

    public static final long MINUTE_IN_MILLIS = 60000;
    public static long COOLDOWN = 0;

    public static boolean hasPassed(long input, Player pl){
        UUID uuid = pl.getUniqueId();
        if(!WarpHandler.getInstance().getLastVote().containsKey(uuid)){
            return true; // They aren't even in the map so they are okay
        }

        long last = WarpHandler.getInstance().getLastVote().get(uuid);
        long now = System.currentTimeMillis();

        return last + COOLDOWN >= now;
    }

    public static void setCooldownMins(long mins) {
        COOLDOWN = MINUTE_IN_MILLIS * mins;
    }
}
