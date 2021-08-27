package dev.kyro.pitsim.enums;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.misc.RenownUpgradeDisplays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RenownUpgrade {
    GOLD_BOOST("Renown Gold Boost", 10, 0, 1, true, 10, RenownUpgradeDisplays.goldBoostCosts),
    XP_BOOST("Renown XP Boost", 10,  1, 1, true, 10, RenownUpgradeDisplays.XPBoostCosts),
    TENACITY("Tenacity", 10,  2, 10, true, 2, RenownUpgradeDisplays.tenacityCosts),
    STREAKER("Perk unlock: Streaker", 20, 3, 25, false, 0, null),
    UBER_INCREASE("Uberstreak Limit Increase", 25, 4, 30, true, 5, RenownUpgradeDisplays.uberIncreaseCosts),
    DIVINE_INTERVENTION("Divine Intervention", 25, 5, 25, true, 3, RenownUpgradeDisplays.divineCosts),
    WITHERCRAFT("Withercraft", 50, 6, 30, false, 0, null),
    FIRST_STRIKE("Perk unlock: First Strike", 15, 7, 10, false, 0, null),
    IMPATIENT("Impatient", 10, 8, 10, false, 0, null);


    public String refName;
    public int renownCost;
    public int slot;
    public int levelReq;
    public boolean isTiered;
    public int maxTiers;
    public List<Integer> tierCosts;

    RenownUpgrade(String refName, int renownCost, int slot, int levelReq, boolean
                  isTiered, int maxTiers, List<Integer> tierCosts) {
        this.refName = refName;
        this.renownCost = renownCost;
        this.slot = slot;
        this.levelReq = levelReq;
        this.isTiered = isTiered;
        this.maxTiers = maxTiers;
        this.tierCosts = tierCosts;
    }

    public static boolean hasUpgrade(Player player, RenownUpgrade upgrade) {
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        return playerData.contains(upgrade.name());
//        return playerData.contains(upgrade.name()) && playerData.getBoolean(upgrade.name());

    }

    public static int getTier(Player player, RenownUpgrade upgrade) {
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        if(!playerData.contains(upgrade.name())) return 0;
        else return playerData.getInt(upgrade.name());

    }
}
