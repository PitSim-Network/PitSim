package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Parasite extends PitEnchant {

	public Parasite() {
		super("Parasite", false, ApplyType.BOWS,
				"parasite", "para");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, 40);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		attackEvent.attacker.setHealth(Math.min(attackEvent.attacker.getHealth() + getHealing(enchantLvl), attackEvent.attacker.getMaxHealth()));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal &c" + Misc.getHearts(getHealing(enchantLvl)) + " &7on arrow hit (2s cooldown)").getLore();
	}

	public double getHealing(int enchantLvl) {

		return Math.floor(Math.pow(enchantLvl, 1.4)) * 0.5;
	}
}
