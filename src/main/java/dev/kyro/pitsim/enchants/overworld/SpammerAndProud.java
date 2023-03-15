package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class SpammerAndProud extends PitEnchant {

	public SpammerAndProud() {
		super("Spammer and Proud", false, ApplyType.BOWS,
				"spammerandproud", "sap", "spamandproud", "proud");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		if(!attackEvent.isAttackerPlayer()) return;
		if(attackEvent.getAttacker().getLocation().distance(attackEvent.getDefender().getLocation()) > 8) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage when shooting within &f4 &7blocks"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases the " +
				"damage your arrows deal when you are close to your target";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 7 + 11;
	}
}
