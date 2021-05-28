package dev.kyro.pitsim.controllers;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PitPerk implements Listener {

    public static PitPerk INSTANCE;

    public String name;
    public ItemStack displayItem;
    public int guiSlot;

    public PitPerk(String name, ItemStack displayItem, int guiSlot) {
        INSTANCE = this;
        this.name = name;
        this.displayItem = displayItem;
        this.guiSlot = guiSlot;
    }

    public abstract List<String> getDescription();

    public boolean playerHasUpgrade(Player player) {

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        for(PitPerk pitPerk : pitPlayer.pitPerks) {

            if(pitPerk == this) return true;
        }
        return false;
    }
}
