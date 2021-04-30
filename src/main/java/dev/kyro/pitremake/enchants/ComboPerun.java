package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;

import java.util.List;

public class ComboPerun extends PitEnchant {

	public ComboPerun() {
		super("Combo: Perun's Wrath", true, ApplyType.SWORDS,
				"perun", "lightning");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return damageEvent;

		damageEvent.trueDamage += getTrueDamage(enchantLvl);
		damageEvent.defender.getWorld().strikeLightningEffect(damageEvent.defender.getLocation());

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 3) {

			return new ALoreBuilder("&7Every &efourth &7hit strikes", "&elightning &7for &c1\u2764 &7+ &c1\u2764",
					"&7per &bdiamond piece &7on your", "&7victim.", "&7&oLightning deals true damage").getLore();
		}

		return new ALoreBuilder("&7Every&e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7hit strikes",
				"&elightning for &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + "&7.", "&7&oLightning deals true damage").getLore();
	}

	public double getTrueDamage(int enchantLvl) {

		return enchantLvl + 2;
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(6 - enchantLvl, 1);
	}
}
