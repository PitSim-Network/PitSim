package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Singularity extends PitEnchant {
	public static Singularity INSTANCE;

	public Singularity() {
		super("Singularity", true, ApplyType.PANTS,
				"singularity", "sing");
		INSTANCE = this;
	}

	public static double getAdjustedFinalDamage(AttackEvent attackEvent) {
		double finalDamage = attackEvent.getWrapperEvent().getSpigotEvent().getFinalDamage();
		if(attackEvent.isDefenderRealPlayer() && attackEvent.getDefenderPitPlayer().megastreak.isOnMega()) return finalDamage;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(INSTANCE);
		if(enchantLvl == 0) return finalDamage;

		double maxDamage = getMaxDamage(enchantLvl);
		return Math.min(finalDamage, maxDamage);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Hits you receive deal at most &c" + Misc.getHearts(getMaxDamage(enchantLvl)) + " &7damage"
		).getLore();
	}

	public static double getMaxDamage(int enchantLvl) {
		return Math.max(4.8 - enchantLvl * 0.4, 0);
	}
}
