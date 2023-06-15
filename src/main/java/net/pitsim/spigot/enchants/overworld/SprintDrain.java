package net.pitsim.spigot.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SprintDrain extends PitEnchant {

	public SprintDrain() {
		super("Sprint Drain", false, ApplyType.BOWS,
				"sprintdrain", "drain", "sprint", "sprint-drain", "sd");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker().equals(attackEvent.getDefender())) return;

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW, getSlowDuration(enchantLvl) * 20, 0, true, false);
		Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.SPEED,
				getSpeedDuration(enchantLvl) * 20, getSpeedAmplifier(enchantLvl) - 1, true, false);

		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		pitAttacker.stats.drain++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		if(enchantLvl == 1) {
			return new PitLoreBuilder(
					"&7Arrow shots grant you &eSpeed " + AUtil.toRoman(getSpeedAmplifier(enchantLvl)) +
					"&7(" + getSpeedDuration(enchantLvl) + "s)"
			).getLore();
		} else {
			return new PitLoreBuilder(
					"&7Arrow shots grant you &eSpeed " + AUtil.toRoman(getSpeedAmplifier(enchantLvl)) +
					" &7(" + getSpeedDuration(enchantLvl) + "s) and apply &9Slowness I &7(" +
					getSlowDuration(enchantLvl) + "s)"
			).getLore();
		}
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that gives you " +
				"&eSpeed &7when you shoot your opponent, and also afflicts them with &9Slowness";
	}

	public int getSlowDuration(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 0) * 3;
	}

	public int getSpeedAmplifier(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 1);
	}

	public int getSpeedDuration(int enchantLvl) {

		return enchantLvl * 2 + 1;
	}
}
