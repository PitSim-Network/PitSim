package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.Sound;

import java.util.List;

public class Perun extends PitEnchant {

	public Perun() {
		super("Combo: Perun's Wrath", true, ApplyType.SWORDS,
				"perun", "lightning");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.attacker, this);
		if(enchantLvl == 0) return damageEvent;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		System.out.println(pitPlayer.enchantHits);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return damageEvent;

		damageEvent.trueDamage += getTrueDamage(enchantLvl);
		damageEvent.defender.getWorld().strikeLightningEffect(damageEvent.defender.getLocation());
		ASound.play(damageEvent.attacker, Sound.ORB_PICKUP, 1, 0.6F);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 3) {

			return new ALoreBuilder("&7Every &efourth &7hit strikes", "&elightning &7for &c1\u2764 &7+ &c1\u2764",
					"&7per &bdiamond piece &7on your", "&7victim.", "&7&oLightning deals true damage").getLore();
		}

		double inHearts = (getTrueDamage(enchantLvl) / 2);
		String asString = Double.toString(inHearts).endsWith(".0") ? Double.toString(inHearts).split(".0")[0] : Double.toString(inHearts);
		return new ALoreBuilder("&7Every&e" + ordinalWords(getStrikes(enchantLvl)) + " &7hit strikes",
				"&elightning for &c" + asString + "\u2764&7.", "&7&oLightning deals true damage").getLore();
	}

	public double getTrueDamage(int enchantLvl) {

		return enchantLvl + 2;
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(6 - enchantLvl, 1);
	}

	public String ordinalWords(int num) {

		switch(num) {
			case 1:
				return "";
			case 2:
				return " second";
			case 3:
				return " third";
			case 4:
				return " fourth";
			case 5:
				return " fifth";
		}
		return "";
	}
}
