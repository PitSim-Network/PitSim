package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Parasite extends PitEnchant {

	public Parasite() {
		super("Parasite", false, ApplyType.BOWS,
				"parasite", "para");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.attacker.setHealth(Math.min(attackEvent.attacker.getHealth() + getHealing(enchantLvl), attackEvent.attacker.getMaxHealth()));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal &c" + getHealing(enchantLvl) / 2 + "\u2764 &7on arrow hit").getLore();
	}

//	TODO: Fletching damage equation
	public double getHealing(int enchantLvl) {


		switch(enchantLvl) {
			case 1:
				return 0.5;
			case 2:
				return 1.0;
			case 3:
				return 2.0;

		}

		return 0.0;
	}
}
