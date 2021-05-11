package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;

import java.util.List;

public class Bruiser extends PitEnchant {

	public Bruiser() {
		super("Bruiser", false, ApplyType.SWORDS,
				"bruiser");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0 || !damageEvent.defender.isBlocking()) return damageEvent;
		damageEvent.decrease += getDamageReduction(enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Blocking with your swords reduces", "received damage by &c" + Misc.getHearts(getDamageReduction(enchantLvl))).getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return Math.floor(Math.pow(enchantLvl, 1.3) * 0.5);
	}
}
