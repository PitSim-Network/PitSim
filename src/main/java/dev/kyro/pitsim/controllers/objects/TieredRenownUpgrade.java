package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class TieredRenownUpgrade extends RenownUpgrade {

	public TieredRenownUpgrade(String name, String refName, int prestigeReq) {
		super(name, refName, prestigeReq);
	}

	public TieredRenownUpgrade(String name, String refName, int prestigeReq, Class<? extends AGUIPanel> subPanel) {
		super(name, refName, prestigeReq, subPanel);
	}

	public abstract String getCurrentEffect(int tier);
	public abstract String getEffectPerTier();
	public abstract List<Integer> getTierCosts();

	public int getMaxTiers() {
		return getTierCosts().size();
	}

	@Override
	public PitLoreBuilder getBaseDescription(Player player) {
		boolean hasUpgrade = UpgradeManager.hasUpgrade(player, this);
		int tier = UpgradeManager.getTier(player, this);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();
		if(hasUpgrade) {
			loreBuilder.addLongLine("&7Current: " + getCurrentEffect(tier));
			loreBuilder.addLongLine("&7Tier: &a" + AUtil.toRoman(tier), false);
		}
		loreBuilder.attemptAddSpacer();
		loreBuilder.addLore("&7Each Tier:");
		loreBuilder.addLongLine(getEffectPerTier(), false);
		return loreBuilder;
	}
}
