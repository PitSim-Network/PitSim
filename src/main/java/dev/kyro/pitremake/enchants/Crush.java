package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.Cooldown;
import dev.kyro.pitremake.controllers.CooldownManager;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Crush extends PitEnchant {

	public Crush() {
		super("Crush", false, ApplyType.SWORDS,
				"crush");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		Cooldown cooldown = getCooldown(damageEvent.attacker, 2 * 20);
		if(cooldown.isOnCooldown()) return damageEvent; else cooldown.reset();

		Misc.applyPotionEffect(damageEvent.defender, PotionEffectType.WEAKNESS, (int) (getDuration(enchantLvl) * 20), enchantLvl + 3);
		damageEvent.attacker.playSound(damageEvent.attacker.getLocation(), Sound.GLASS, 1, 0.80F);
		damageEvent.defender.playSound(damageEvent.defender.getLocation(), Sound.GLASS, 1, 0.80F);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Strikes apply &cWeakness " + AUtil.toRoman(enchantLvl + 4), "&7(lasts " + getDuration(enchantLvl) +
				"s, 2s cooldown)").getLore();
	}

	//	TODO: Crush damage calculation
	
	public double getDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0.2;
			case 2:
				return 0.4;
			case 3:
				return 0.5;

		}

		return 0;
	}
}
