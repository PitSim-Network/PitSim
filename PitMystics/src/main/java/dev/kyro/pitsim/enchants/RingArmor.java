package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class RingArmor extends PitEnchant {

	public RingArmor() {
		super("Ring Armor", false, ApplyType.PANTS,
				"ring", "armor", "ring-armor");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.arrow == null) return;
		attackEvent.multiplier.add(getDamageMultiplier(enchantLvl));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage from", "&7arrows").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return (100D - getDamageReduction(enchantLvl)) / 100;
	}

//	TODO: Ring Armor damage reduction equation

	public int getDamageReduction(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 20;
			case 2:
				return 40;
			case 3:
				return 60;

		}

		return 0;
	}
}
