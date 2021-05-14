package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Sharp extends PitEnchant {

	public Sharp() {
		super("Sharp", false, ApplyType.SWORDS,
				"sharp", "s");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7melee damage").getLore();
	}

	//	TODO: Sharp damage calculation
	
	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 4;
			case 2:
				return 7;
			case 3:
				return 12;

		}

		return 0;
	}
}
