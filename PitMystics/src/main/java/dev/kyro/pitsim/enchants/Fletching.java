package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Fletching extends PitEnchant {

	public Fletching() {
		super("Fletching", false, ApplyType.BOWS,
				"fletch", "fletching");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + " &7bow damage").getLore();
	}

//	TODO: Fletching damage equation
	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 7;
			case 2:
				return 12;
			case 3:
				return 20;
			case 20:
				return 100;
		}

		return 0;
	}
}