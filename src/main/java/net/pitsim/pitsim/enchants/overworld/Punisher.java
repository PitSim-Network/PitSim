package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Punisher extends PitEnchant {

	public Punisher() {
		super("Punisher", false, ApplyType.MELEE,
				"pun", "punisher");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getDefender().getHealth() / attackEvent.getDefender().getMaxHealth() > 0.5) return;
		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players below 50% HP"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that deals " +
				"more damage vs players that are under half health";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 6 + 6;
	}
}
