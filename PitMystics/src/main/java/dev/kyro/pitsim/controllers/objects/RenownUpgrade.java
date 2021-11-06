package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class RenownUpgrade implements Listener {

    public static List<RenownUpgrade> upgrades = new ArrayList<>();

    public String name;
    public String refName;
    public int renownCost;
    public int guiSlot;
    public int prestigeReq;
    public boolean isTiered;
    public int maxTiers;


    public RenownUpgrade INSTANCE;

    public RenownUpgrade(String name, String refName, int renownCost, int guiSlot, int prestigeReq, boolean isTiered, int maxTiers) {
//        INSTANCE = this;
        this.name = name;
        this.refName = refName;
        this.renownCost = renownCost;
        this.guiSlot = guiSlot;
        this.prestigeReq = prestigeReq;
        this.isTiered = isTiered;
        this.maxTiers = maxTiers;


        upgrades.add(this);
    }

    public abstract ItemStack getDisplayItem(Player player, boolean isCustomPanel);

    public abstract List<Integer> getTierCosts();

    public abstract AGUIPanel getCustomPanel();






}
