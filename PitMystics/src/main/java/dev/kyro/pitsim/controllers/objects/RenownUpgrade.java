package dev.kyro.pitsim.controllers.objects;

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
    public int levelReq;
    public boolean isTiered;
    public int maxTiers;


    public RenownUpgrade INSTANCE;

    public RenownUpgrade(String name, String refName, int renownCost, int guiSlot, int levelReq, boolean isTiered, int maxTiers) {
//        INSTANCE = this;
        this.name = name;
        this.refName = refName;
        this.renownCost = renownCost;
        this.guiSlot = guiSlot;
        this.levelReq = levelReq;
        this.isTiered = isTiered;
        this.maxTiers = maxTiers;


        upgrades.add(this);
    }

    public abstract ItemStack getDisplayItem(Player player);

    public abstract List<Integer> getTierCosts();






}
