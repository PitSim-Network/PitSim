package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Mirror extends PitEnchant {

	public Mirror() {
		super("Mirror", false, ApplyType.PANTS,
				"mirror", "mir");
		isUncommonEnchant = true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(defenderEnchantLvl == 0) return;
		attackEvent.trueDamage *= Misc.getReductionMultiplier(getReductionPercent(defenderEnchantLvl));

//		If just opponent has mirror
//		if(defenderEnchantLvl != 0) {
//
//			double trueDamage = attackEvent.trueDamage;
//			trueDamage *= getReflectionPercent(defenderEnchantLvl) / 100;
//			if(attackerEnchantLvl == 0) attackEvent.selfTrueDamage += trueDamage;
//		}
//
//		attackEvent.selfTrueDamage = 0;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl >= 3) {

			return new ALoreBuilder("&7You are immune to true damage").getLore();
		} else {

			return new ALoreBuilder("&7You take &9" + getReductionPercent(enchantLvl) + "% &7less true damage").getLore();
		}
	}

	public static int getReductionPercent(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 20;
			case 2:
				return 50;
		}
		return 100;
	}

	public double getReflectionPercent(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 0;
			case 2:
				return 25;
			case 3:
				return 50;

		}
		return 0;
	}
}
