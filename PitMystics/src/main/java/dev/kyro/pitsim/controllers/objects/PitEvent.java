package dev.kyro.pitsim.controllers.objects;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public abstract class PitEvent implements Listener {

    public String name;
    public Boolean isMajor;
    public int minutes;
    public ChatColor color;
    public String abreviatedName;

    public PitEvent(String name, int minutes, Boolean isMajor, String abreviatedName, ChatColor color) {
        this.name = name;
        this.minutes = minutes;
        this.isMajor = isMajor;
        this.color = color;
        this.abreviatedName = abreviatedName;
    }

    public abstract String getName();
    public abstract void prepare();
    public abstract void start() throws Exception;
    public abstract void end();
}
