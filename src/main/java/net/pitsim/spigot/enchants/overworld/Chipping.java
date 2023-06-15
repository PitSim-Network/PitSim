package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Chipping extends PitEnchant {

	public Chipping() {
		super("Chipping", false, ApplyType.BOWS,
				"chipping", "chip");
		isUncommonEnchant = true;
	}

	@Override
	public boolean isEnabled() {
		return PitSim.status.isOverworld();
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || !attackEvent.getArrow().isCritical()) return;

		attackEvent.trueDamage += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Fully charged shots deal &c+" + Misc.getHearts(getDamage(enchantLvl)) + " &7true damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that " +
				"deals true damage on fully charged bow shots";
	}

	public double getDamage(int enchantLvl) {
		if(enchantLvl == 1) return 0.5;
		return enchantLvl - 1;
	}
}
