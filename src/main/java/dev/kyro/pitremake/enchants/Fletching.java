package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Fletching extends PitEnchant {

	public Fletching() {
		super("Fletching", false, ApplyType.BOWS,
				"fletch", "fletching");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.defender.getHealth() / damageEvent.defender.getMaxHealth() > 0.5) return damageEvent;
		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + " &7bow damage").getLore();
	}

	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 7;
			case 2:
				return 12;
			case 3:
				return 20;
			case 20:
				return 100;
		}

		return 0;
	}
}
