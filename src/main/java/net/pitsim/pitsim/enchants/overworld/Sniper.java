package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Sniper extends PitEnchant {

	public Sniper() {
		super("Sniper", false, ApplyType.BOWS,
				"sniper", "snipe");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		if(!attackEvent.isAttackerPlayer()) return;
		if(attackEvent.getAttacker().getLocation().distance(attackEvent.getDefender().getLocation()) < 16) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage when shooting from over &f16 &7blocks"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that makes your arrows " +
				"deal more damage when you are far away from your target";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 12 + 48;
	}
}
