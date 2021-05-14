package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.controllers.PitPlayer;

import java.util.List;

public class ComboSwift extends PitEnchant {

	public ComboSwift() {
		super("Combo: Swift", false, ApplyType.SWORDS,
				"comoswift", "swift", "cs", "combo-swift");
	}

	@EventHandler
	public void onDamage(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getCombo(enchantLvl))) return;

		Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.SPEED, (int) (enchantLvl + 2) * 20,
				getSpeedAmplifier(enchantLvl) - 1, true, false);
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
