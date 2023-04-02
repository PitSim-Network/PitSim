package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class XPBoost extends TieredRenownUpgrade {
	public static XPBoost INSTANCE;

	public XPBoost() {
		super("Renown XP Boost", "XP_BOOST", 1);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		killEvent.xpReward += 5 * tier;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.EXP_BOTTLE)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&b+" + (tier * 5) + " XP";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Earn &b+5 XP &7from kills";
	}

	@Override
	public String getSummary() {
		return "&eRenown &bXP Boost &7is an &erenown &7upgrade that grants extra &b+5XP &7on a player/bot kill per tier";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	}
}
