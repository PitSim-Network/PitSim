package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SprintDrain extends PitEnchant {

	public SprintDrain() {
		super("Sprint Drain", false, ApplyType.BOWS,
				"sprintdrain", "drain", "sprint", "sprint-drain", "sd");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.attacker.equals(damageEvent.defender)) return damageEvent;

		Misc.applyPotionEffect(damageEvent.defender, PotionEffectType.SLOW, getSlowDuration(enchantLvl) * 20, 0, true, false);
		Misc.applyPotionEffect(damageEvent.attacker, PotionEffectType.SPEED,
				getSpeedDuration(enchantLvl) * 20, getSpeedModifier(enchantLvl) - 1, true, false);

		return damageEvent;
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
