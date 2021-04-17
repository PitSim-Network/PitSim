package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Protection extends PitEnchant {

	public Protection() {
		super("Protection", false, ApplyType.PANTS,
				"prot", "protection");
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

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return (100D - getDamageReduction(enchantLvl)) / 100;
	}

	public int getDamageReduction(int enchantLvl) {

		return (int) Math.max(Math.floor(Math.pow(enchantLvl, 1.3) * 2) + 2, 0);
	}
}
