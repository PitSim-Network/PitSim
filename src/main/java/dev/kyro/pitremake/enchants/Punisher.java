package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class Punisher extends PitEnchant {

	public Punisher() {
		super("Punisher", false, ApplyType.SWORDS,
				"pun", "punisher");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.attacker, this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.defender.getHealth() / damageEvent.defender.getMaxHealth() > 0.5) return damageEvent;
		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players", "&7below 50% HP").getLore();
	}

	public int getDamage(int enchantLvl) {

		return enchantLvl * 6;
	}
}
