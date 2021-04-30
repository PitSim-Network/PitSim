package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;

import java.util.List;

public class KingBuster extends PitEnchant {

	public KingBuster() {
		super("King Buster", false, ApplyType.SWORDS,
				"kb", "kingbuster", "kbuster", "king-buster");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.defender.getHealth() / damageEvent.defender.getMaxHealth() < 0.5) return damageEvent;
		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players", "&7above 50% HP").getLore();
	}

	public int getDamage(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.5) * 3.2) + 4);
	}
}
