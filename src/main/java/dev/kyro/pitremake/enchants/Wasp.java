package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Wasp extends PitEnchant {

	public Wasp() {
		super("Wasp", false, ApplyType.BOWS,
				"wasp");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		Misc.applyPotionEffect(damageEvent.defender, PotionEffectType.WEAKNESS, getDuration(enchantLvl) * 20, enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Apply &cWeakness " + AUtil.toRoman(enchantLvl + 1) + " &7(" +
				getDuration(enchantLvl) + "s) on hit").getLore();
	}

//	TODO: Wasp damage equation
	public int getDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 6;
			case 2:
				return 11;
			case 3:
				return 16;

		}

		return 0;
	}
}
