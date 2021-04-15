package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.NumberFormatter;

import java.util.List;

public class Solitude extends PitEnchant {

	public Solitude() {
		super("Solitude", true, ApplyType.PANTS,
				"solitude", "soli");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.multiplier.add(getDamageMultiplier(enchantLvl));

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage when &9" + NumberFormatter.convert(getNearbyPlayers(enchantLvl)),
				"&7or less players are within 7", "&7blocks").getLore();
	}

	public int getNearbyPlayers(int enchantLvl) {

		return (int) (enchantLvl * 0.5 + 1);
	}

	public double getDamageMultiplier(int enchantLvl) {

		return Math.max(0.7 - ((double) enchantLvl / 10), 0);
	}

	public int getDamageReduction(int enchantLvl) {

		return (int) (100 - getDamageMultiplier(enchantLvl) * 100);
	}
}
