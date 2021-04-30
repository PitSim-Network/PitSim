package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Chipping extends PitEnchant {

	public Chipping() {
		super("Chipping", false, ApplyType.BOWS,
				"chipping", "chip");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.trueDamage += getDamage(enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deals &c" + getDamage(enchantLvl) + "\u2764 &7extra true damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		return enchantLvl * 0.5;
	}
}
