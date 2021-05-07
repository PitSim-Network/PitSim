package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ComboSwift extends PitEnchant {

	public ComboSwift() {
		super("Combo: Swift", false, ApplyType.SWORDS,
				"comoswift", "swift", "cs", "combo-swift");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getCombo(enchantLvl))) return damageEvent;

		Misc.applyPotionEffect(damageEvent.attacker, PotionEffectType.SPEED, (int) (enchantLvl + 2) * 20,
				getSpeedAmplifier(enchantLvl) - 1);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every&e" + Misc.ordinalWords(getCombo(enchantLvl)) + " &7strike gain",
				"&eSpeed " + AUtil.toRoman(getSpeedAmplifier(enchantLvl)) + " &7(" + (enchantLvl + 2) + "s)").getLore();

	}

	//	TODO: Sharp damage calculation

	public int getSpeedAmplifier(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
			case 3:
				return 2;

		}

		return 0;
	}

	public int getCombo(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 4;
			case 2:
				return 3;
			case 3:
				return 3;

		}

		return 0;
	}
}
