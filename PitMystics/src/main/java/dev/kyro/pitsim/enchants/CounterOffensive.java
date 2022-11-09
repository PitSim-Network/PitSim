package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CounterOffensive extends PitEnchant {

	public CounterOffensive() {
		super("Counter-Offensive", false, ApplyType.PANTS,
				"counteroffensive", "counter-offensive", "co", "offensive");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.defenderIsPlayer) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		HitCounter.incrementCounter(attackEvent.defenderPlayer, this);
		if(!HitCounter.hasReachedThreshold(attackEvent.defenderPlayer, this, getCombo(enchantLvl))) return;
		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SPEED, getSeconds(enchantLvl) * 20, getAmplifier(enchantLvl), false, false);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain &eSpeed II &7(" + getSeconds(enchantLvl) + "s) when hit",
				"&e" + getCombo(enchantLvl) + " times &7by a player").getLore();
	}

	public int getCombo(int enchantLvl) {
		return Math.max(6 - enchantLvl, 1);
	}

	public int getAmplifier(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, 0);
	}

	public int getSeconds(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, 2);
	}
}
