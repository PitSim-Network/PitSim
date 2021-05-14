package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SprintDrain extends PitEnchant {

	public SprintDrain() {
		super("Sprint Drain", false, ApplyType.BOWS,
				"sprintdrain", "drain", "sprint", "sprint-drain", "sd");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.attacker.equals(attackEvent.defender)) return;

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, getSlowDuration(enchantLvl) * 20, 0, true, false);
		Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.SPEED,
				getSpeedDuration(enchantLvl) * 20, getSpeedModifier(enchantLvl) - 1, true, false);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {


		if(enchantLvl == 1) {
			return new ALoreBuilder("&7Arrow shots grant you &eSpeed " + AUtil.toRoman(getSpeedModifier(enchantLvl)), "&7(" +
					getSpeedDuration(enchantLvl) + "s)").getLore();
		} else {
			return new ALoreBuilder("&7Arrow shots grant you &eSpeed " + AUtil.toRoman(getSpeedModifier(enchantLvl)), "&7(" +
					getSpeedDuration(enchantLvl) + "s) and apply &9Slowness I ", "&7(" + getSlowDuration(enchantLvl) + "s)").getLore();
		}

	}

//	TODO: Sprint drain equations
	public int getSlowDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0;
			case 2:
				return 3;
			case 3:
				return 3;

		}

		return 0;
	}

	public int getSpeedModifier(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
				return 1;
			case 3:
				return 2;

		}

		return 0;
	}

	public int getSpeedDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 3;
			case 2:
				return 5;
			case 3:
				return 7;

		}

		return 0;
	}
}
