package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class HelmetAbility implements Listener {

    public static List<HelmetAbility> helmetAbilities = new ArrayList<>();
    public static List<GoldenHelmet> toggledHelmets = new ArrayList<>();

    public Player player;
    public String name;
    public String refName;
    public boolean isTogglable;
    public int slot;

    public HelmetAbility(Player player, String name, String refName, boolean isTogglable, int slot) {
        this.player = player;
        this.name = name;
        this.refName = refName;
        this.isTogglable = isTogglable;
        this.slot = slot;
    }

    public void onActivate() { }
    public void onDeactivate() { }
    public void onProc() { }

    public abstract List<String> getDescription();
    public abstract ItemStack getDisplayItem();

    public static HelmetAbility getAbility(String refName) {
        for(HelmetAbility helmetAbility : helmetAbilities) {
            if(helmetAbility.refName.equals(refName)) return helmetAbility;
        }
        return null;
    }


    public static void registerHelmetAbility(HelmetAbility helmetAbility) {

        helmetAbilities.add(helmetAbility);
        PitSim.INSTANCE.getServer().getPluginManager().registerEvents(helmetAbility, PitSim.INSTANCE);
    }

}
