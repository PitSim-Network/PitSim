package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.Sound;

import java.util.List;

public class Gamble extends PitEnchant {

	public Gamble() {
		super("Gamble", true, ApplyType.SWORDS,
				"gamble");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.attacker, this);
		if(enchantLvl == 0) return damageEvent;

		if(Math.random() < 0.5) {
			damageEvent.trueDamage += getTrueDamage(enchantLvl);
			ASound.play(damageEvent.attacker, Sound.NOTE_PLING, 1, 3F);
		} else {
			damageEvent.selfTrueDamage += getTrueDamage(enchantLvl);
			ASound.play(damageEvent.attacker, Sound.NOTE_PLING, 1, 1.5F);
		}

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&d50% chance &7to deal &c" + (getTrueDamage(enchantLvl) / 2) + "\u2764 &7true",
				"&7damage to whoever you hit, or to", "&7yourself").getLore();
	}

	public int getTrueDamage(int enchantLvl) {

		return enchantLvl * 2;
	}
}
