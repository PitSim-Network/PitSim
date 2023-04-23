package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.aitems.misc.VeryYummyBread;
import dev.kyro.pitsim.aitems.misc.YummyBread;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PlayerItemLocation;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BreadDealer extends TieredRenownUpgrade {
	public static BreadDealer INSTANCE;

	public BreadDealer() {
		super("Bread Dealer", "BREADDEALER", 30);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.getDeadPlayer(), this)) return;
		double percentSaved = UpgradeManager.getTier(killEvent.getDeadPlayer(), this) * getPercentPerTier();
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : new ArrayList<>(killEvent.getVulnerableItems().entrySet())) {
			PlayerItemLocation itemLocation = entry.getKey();
			KillEvent.ItemInfo itemInfo = entry.getValue();
			if(!(itemInfo.pitItem instanceof YummyBread) && !(itemInfo.pitItem instanceof VeryYummyBread)) continue;
			killEvent.removeVulnerableItem(itemLocation);

			ItemStack modifiedStack = itemInfo.itemStack.clone();
			int newAmount = (int) Math.floor(modifiedStack.getAmount() * percentSaved / 100.0);
			modifiedStack.setAmount(newAmount);
			killEvent.getDeadInventoryWrapper().putItem(itemLocation, modifiedStack);
		}
		killEvent.getDeadPlayer().updateInventory();
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.BREAD)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&7Retain &e" + (tier * getPercentPerTier()) + "% &7bread";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Retain &e+" + getPercentPerTier() + "% &7bread on death";
	}

	@Override
	public String getSummary() {
		return "&eBread Dealer&7 is a &erenown&7 upgrade that allows you to retain some of the bread in your inventory on death";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 20, 30, 40, 50);
	}
	
	public static int getPercentPerTier() {
		return 10;
	}
}
