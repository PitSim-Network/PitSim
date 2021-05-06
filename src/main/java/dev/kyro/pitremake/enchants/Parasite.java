package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Parasite extends PitEnchant {

	public Parasite() {
		super("Parasite", false, ApplyType.BOWS,
				"parasite", "para");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.attacker.setHealth(Math.min(damageEvent.attacker.getHealth() + getHealing(enchantLvl), damageEvent.attacker.getMaxHealth()));

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal &c" + getHealing(enchantLvl) / 2 + "\u2764 &7on arrow hit").getLore();
	}

//	TODO: Fletching damage equation
	public double getHealing(int enchantLvl) {


		switch(enchantLvl) {
			case 1:
				return 0.5;
			case 2:
				return 1.0;
			case 3:
				return 2.0;

		}

		return 0.0;
	}
}
