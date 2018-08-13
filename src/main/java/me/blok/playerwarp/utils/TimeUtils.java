package me.blok.playerwarp.utils;

public class TimeUtils {

    public static final long DAY_IN_MILLIS = 86400000;

    public static boolean hasDayPassed(long input){
        long now = System.currentTimeMillis();
        return input + DAY_IN_MILLIS >= now;
    }

}
