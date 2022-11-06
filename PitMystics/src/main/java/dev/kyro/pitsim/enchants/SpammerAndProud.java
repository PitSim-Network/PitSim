package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
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
		if(!attackEvent.attackerIsPlayer) return;
		if(attackEvent.attacker.getLocation().distance(attackEvent.defender.getLocation()) > 8) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage when shooting",
				"&7within &f4 &7blocks").getLore();
	}

	public int getDamage(int enchantLvl) {

		return enchantLvl * 7 + 11;
	}
}
