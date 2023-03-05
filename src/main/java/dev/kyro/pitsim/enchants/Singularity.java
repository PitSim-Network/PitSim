package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;

import java.util.List;

public class Singularity extends PitEnchant {
	public static Singularity INSTANCE;

	public Singularity() {
		super("Singularity", true, ApplyType.PANTS,
				"singularity", "sing");
		INSTANCE = this;
	}

	public static double getAdjustedFinalDamage(AttackEvent attackEvent) {
		double finalDamage = attackEvent.getEvent().getFinalDamage();
		if(PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) && attackEvent.getDefenderPitPlayer().megastreak.isOnMega()) return finalDamage;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(INSTANCE);
		if(enchantLvl == 0) return finalDamage;

		double maxDamage = getMaxDamage(enchantLvl);
		return Math.min(finalDamage, maxDamage);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder(
				"&7Hits you receive deal at most",
				"&c" + Misc.getHearts(getMaxDamage(enchantLvl)) + " &7damage"
		).getLore();
	}

	public static double getMaxDamage(int enchantLvl) {
		return Math.max(3.2 - enchantLvl * 0.4, 0);
	}
}
