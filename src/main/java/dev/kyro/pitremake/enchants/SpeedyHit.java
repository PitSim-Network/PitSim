package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.Cooldown;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SpeedyHit extends PitEnchant {

	public SpeedyHit() {
		super("Speedy Hit", true, ApplyType.SWORDS,
				"speedyhit", "speedy", "speed", "sh", "speedy-hit");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		Cooldown cooldown = getCooldown(damageEvent.attacker,(getCooldown(enchantLvl) * 20));
		if(cooldown.isOnCooldown()) return damageEvent; else cooldown.reset();

		Misc.applyPotionEffect(damageEvent.attacker, PotionEffectType.SPEED, (int) (getDuration(enchantLvl) * 20), 0, true, false);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain Speed I for &e" + getDuration(enchantLvl) + "s &7on hit (" +
				getCooldown(enchantLvl) + "s", "&7cooldown)").getLore();
	}

	//	TODO: Sharp damage calculation
	public int getDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 5;
			case 2:
				return 7;
			case 3:
				return 9;

		}

		return 0;
	}

	public int getCooldown(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 3;
			case 2:
				return 2;
			case 3:
				return 1;

		}

		return 0;
	}
}
