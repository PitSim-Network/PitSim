package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.VeryYummyBread;
import dev.kyro.pitsim.aitems.YummyBread;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BreadDealer extends RenownUpgrade {
	public static BreadDealer INSTANCE;

	public BreadDealer() {
		super("Bread Dealer", "BREADDEALER", 10, 0, 1, true, 2);
		INSTANCE = this;
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		int tier = UpgradeManager.getTier(player, this);
		int retainPercent = tier * getPercentPerTier();
		
		ALoreBuilder loreBuilder = new ALoreBuilder();
		if(UpgradeManager.hasUpgrade(player, this)) {
			loreBuilder.addLore(
					"&7Current: Retain &e" + retainPercent + "% &7bread on death",
					"&7Tier: &a" + AUtil.toRoman(tier),
					""
			);
		}
		loreBuilder.addLore(
				"&7Each Tier:",
				"&7Retain &e+" + retainPercent + " &7bread on death"
		);

		return new AItemStackBuilder(Material.BREAD)
				.setName(UpgradeManager.itemNameString(this, player))
				.setLore(loreBuilder)
				.getItemStack();
	}

	public static void handleBreadOnDeath(Player player) {
		int tier = UpgradeManager.getTier(player, BreadDealer.INSTANCE);
		int retainPercent = tier * getPercentPerTier();

		if(!PlayerManager.isRealPlayer(player)) return;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack itemStack = player.getInventory().getItem(i);

			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null) continue;
			if(pitItem.getClass() != YummyBread.class && pitItem.getClass() != VeryYummyBread.class) continue;

			int quantity = itemStack.getAmount();
			quantity *= retainPercent / 100.0;
			if(quantity == 0) {
				player.getInventory().remove(itemStack);
			} else {
				itemStack.setAmount(quantity);
				player.getInventory().setItem(i, itemStack);
			}
		}
		player.updateInventory();
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 50);
	}
	
	public static int getPercentPerTier() {
		return 10;
	}
}
