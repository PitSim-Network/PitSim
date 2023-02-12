package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class XpBump extends PitEnchant {

	public XpBump() {
		super("XP Bump", false, ApplyType.ALL,
				"xpbump", "xpb", "xp-bump");
		levelStacks = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.xpReward += enchantLvl;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Earn &b+" + getXpIncrease(enchantLvl) + "&bXP &7from kills"
		).getLore();
	}

	public int getXpIncrease(int enchantLvl) {
		return enchantLvl * 2;
	}
}
