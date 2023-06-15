package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GoldBoost extends PitEnchant {

	public GoldBoost() {
		super("Gold Boost", false, ApplyType.ALL,
				"goldboost", "gold-boost", "gboost", "boost");
		levelStacks = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.goldMultipliers.add((getGoldIncrease(enchantLvl) / 100D) + 1);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Earn &6+" + getGoldIncrease(enchantLvl) + "% gold (g) &7from kills"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"the gold you get on kill";
	}

	public int getGoldIncrease(int enchantLvl) {

		return enchantLvl * 15;
	}
}
