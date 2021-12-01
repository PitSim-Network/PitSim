package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;

import java.util.List;

public class ComboSwift extends PitEnchant {

	public ComboSwift() {
		super("Combo: Swift", false, ApplyType.SWORDS,
				"comoswift", "swift", "cs", "combo-swift");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.defender) && Regularity.skipIncrement(regLvl)) return;

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

	public int getSpeedAmplifier(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 1);
	}

	public int getCombo(int enchantLvl) {

		return Math.max(Misc.linearEnchant(enchantLvl, -0.5, 4.5), 1);
	}
}
