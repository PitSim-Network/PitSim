package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Gamble extends PitEnchant {

	public Gamble() {
		super("Gamble", true, ApplyType.SWORDS,
				"gamble");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(Math.random() < 0.5) {
			attackEvent.trueDamage += getTrueDamage(enchantLvl);
			ASound.play(attackEvent.attacker, Sound.NOTE_PLING, 1, 3F);
		} else {
			attackEvent.selfTrueDamage += getTrueDamage(enchantLvl);
			ASound.play(attackEvent.attacker, Sound.NOTE_PLING, 1, 1.5F);
		}

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&d50% chance &7to deal &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + " &7true",
				"&7damage to whoever you hit, or to", "&7yourself").getLore();
	}

	public int getTrueDamage(int enchantLvl) {

		return enchantLvl * 2;
	}
}
