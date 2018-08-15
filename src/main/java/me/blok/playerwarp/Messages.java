package me.blok.playerwarp;

import me.blok.playerwarp.object.Warp;
import me.blok.playerwarp.utils.TimeUtils;

import java.util.HashMap;

/**
 * Created by Blok on 8/9/2018.
 */
public class Messages {
    private static Messages ourInstance = new Messages();

    public static Messages getInstance() {
        return ourInstance;
    }

    private Messages() {
    }

    private HashMap<String, String> messages;

    public void setup(){
        this.messages = new HashMap<>();
        for (String key : Settings.getInstance().getMessages().getKeys(false)){
            this.messages.put(key, Settings.getInstance().getMessages().getString(key));
        }
    }

    public String get(String key){
        return messages.getOrDefault(key, null);
    }

    public String translate(String string, Warp warp){
        if(string.contains("%warp%")){
            string = string.replaceAll("%warp%", warp.getName());
        }
        return string;
    }

    public String translate(String toTranslate, long time){
        if(toTranslate.contains("%time%")){
            toTranslate = toTranslate.replaceAll("%time%", TimeUtils.formatMillis(time));
        }

        return toTranslate;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }
}
