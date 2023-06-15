package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.TieredRenownUpgrade;
import net.pitsim.spigot.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class RenownGoldBoost extends TieredRenownUpgrade {
	public static RenownGoldBoost INSTANCE;

	public RenownGoldBoost() {
		super("Renown Gold Boost", "GOLD_BOOST", 3);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.GOLD_NUGGET)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&6+" + decimalFormat.format(tier * 2.5) + "% gold";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Earn &6+2.5% gold &7from kills";
	}

	@Override
	public String getSummary() {
		return "&eRenown &6Gold Boost &7is an renown upgrade that gives you &62.5% extra gold &7on a player/bot kill per tier";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		double percent = 2.5 * tier;

		killEvent.goldMultipliers.add((percent / 100D) + 1);
	}
}
