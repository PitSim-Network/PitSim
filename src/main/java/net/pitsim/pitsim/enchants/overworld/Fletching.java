package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Fletching extends PitEnchant {

	public Fletching() {
		super("Fletching", false, ApplyType.BOWS,
				"fletch", "fletching");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7bow damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that " +
				"increases the damage your arrows deal";
	}

	public int getDamage(int enchantLvl) {
		return (int) (Math.pow(enchantLvl, 1.32) * 4 + 3);
	}
}
