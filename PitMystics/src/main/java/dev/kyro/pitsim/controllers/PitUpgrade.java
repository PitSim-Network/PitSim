package dev.kyro.pitsim.controllers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class PitUpgrade implements Listener {

    public static PitUpgrade INSTANCE;

    public String name;
    public Material displayMaterial;
    public int guiSlot;

    public PitUpgrade(String name, Material displayMaterial, int guiSlot) {
        INSTANCE = this;
        this.name = name;
        this.displayMaterial = displayMaterial;
        this.guiSlot = guiSlot;
    }

    public abstract List<String> getDescription();

    public boolean playerHasUpgrade(Player player) {

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        for(PitUpgrade pitUpgrade : pitPlayer.pitUpgrades) {

            if(pitUpgrade == this) return true;
        }
        return false;
    }
}
