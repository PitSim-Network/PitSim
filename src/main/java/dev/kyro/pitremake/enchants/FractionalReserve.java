package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;

import java.util.List;

public class FractionalReserve extends PitEnchant {

	public FractionalReserve() {
		super("Fractional Reserve", false, ApplyType.PANTS,
				"fractionalreserve", "frac", "frac-reserve", "fractional-reserve", "fracreserve");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-1% damage per",
				"&610,000g &7you have (&9-" + getDamageReduction(enchantLvl) + "%", "&7max)").getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return (int) Math.max(Math.floor(Math.pow(enchantLvl, 1.65) * 3) + 12, 0);
	}
}
