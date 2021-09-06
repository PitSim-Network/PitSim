package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PitPerk implements Listener {

    public static List<PitPerk> pitPerks = new ArrayList<>();

    public String name;
    public String refName;
    public ItemStack displayItem;
    public int guiSlot;
    public boolean renownUnlockable;
    public String upgradeRef;
    public PitPerk INSTANCE;

    public PitPerk(String name, String refName, ItemStack displayItem, int guiSlot, boolean renownUnlockable, String upgradeRef, PitPerk instance) {
//        INSTANCE = this;
        this.name = name;
        this.refName = refName;
        this.displayItem = displayItem;
        this.guiSlot = guiSlot;
        this.renownUnlockable = renownUnlockable;
        this.upgradeRef = upgradeRef;
        this.INSTANCE = instance;

        pitPerks.add(this);
    }

    public abstract List<String> getDescription();

    public boolean playerHasUpgrade(Player player) {

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        for(PitPerk pitPerk : pitPlayer.pitPerks) {

            if(pitPerk == this) return true;
        }
        return false;
    }

    public ItemStack getDisplayItem() {
        return new AItemStackBuilder(displayItem.clone())
                .setLore(getDescription())
                .setName(name)
                .getItemStack();
    }

    public static PitPerk getPitPerk(String refName) {

        for(PitPerk pitPerk : pitPerks) {
            if(pitPerk.refName.equalsIgnoreCase(refName)) return pitPerk;
        }
        return null;
    }
}
