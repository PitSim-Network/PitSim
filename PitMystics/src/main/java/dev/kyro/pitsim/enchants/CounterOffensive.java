package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CounterOffensive extends PitEnchant {

	public CounterOffensive() {
		super("Counter-Offensive", false, ApplyType.PANTS,
				"counteroffensive", "counter-offensive", "co", "offensive");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		HitCounter.incrementCounter(attackEvent.defender, this);
		if(!HitCounter.hasReachedThreshold(attackEvent.defender, this, getCombo(enchantLvl))) return;
		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SPEED, getDuration(enchantLvl) * 20, 1, false, false);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain &eSpeed II &7(" + getDuration(enchantLvl) + "s) when hit",
				"&e" + getCombo(enchantLvl) + " times &7by a player").getLore();
	}

	public int getCombo(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 4;
			case 2:
				return 3;
			case 3:
				return 2;

		}
		return 0;
	}

	public int getDuration(int enchantLvl) {

		return 1 + (enchantLvl * 2);
	}
}
