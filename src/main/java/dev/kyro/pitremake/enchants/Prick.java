package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Prick extends PitEnchant {

	public Prick() {
		super("Prick", false, ApplyType.PANTS,
				"prick", "thorns");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.selfTrueDamage += getDamage(enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enemies hitting you receive", "&c" + getDamage(enchantLvl) + "\u2764 &7true damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		return enchantLvl * 1.25 + 1.25;
	}
}
