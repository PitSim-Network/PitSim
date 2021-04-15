package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Executioner extends PitEnchant {

	public Executioner() {
		super("Executioner", true, ApplyType.SWORDS,
				"executioner", "exe");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.attacker, this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.executeUnder = getExecuteHealth(enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting an enemy below &c" + (getExecuteHealth(enchantLvl) / 2) + "\u2764",
				"&7instantly kills them").getLore();
	}

	public double getExecuteHealth(int enchantLvl) {

		return enchantLvl + 0.5;
	}
}
