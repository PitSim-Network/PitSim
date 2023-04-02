package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.VeryYummyBread;
import dev.kyro.pitsim.aitems.misc.YummyBread;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BreadDealer extends TieredRenownUpgrade {
	public static BreadDealer INSTANCE;

	public BreadDealer() {
		super("Bread Dealer", "BREADDEALER", 1);
		INSTANCE = this;
	}

	public static void handleBreadOnDeath(Player player) {
		int tier = UpgradeManager.getTier(player, BreadDealer.INSTANCE);
		int retainPercent = tier * getPercentPerTier();

		if(!PlayerManager.isRealPlayer(player)) return;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack itemStack = player.getInventory().getItem(i);

			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!(pitItem instanceof YummyBread) && !(pitItem instanceof VeryYummyBread)) continue;

			int quantity = itemStack.getAmount();
			quantity *= retainPercent / 100.0;
			if(quantity == 0) {
				player.getInventory().setItem(i, new ItemStack(Material.AIR));
			} else {
				itemStack.setAmount(quantity);
				player.getInventory().setItem(i, itemStack);
			}
		}
	}

	@Override
	public ItemStack getBaseItemStack() {
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
		return "&eBread Dealer&7 is an &erenown&7 upgrade that allows you to retain some of the bread in your inventory on death";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 50);
	}
	
	public static int getPercentPerTier() {
		return 10;
	}
}
