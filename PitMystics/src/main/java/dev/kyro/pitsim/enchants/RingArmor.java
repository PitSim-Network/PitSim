package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class RingArmor extends PitEnchant {

	public RingArmor() {
		super("Ring Armor", false, ApplyType.PANTS,
				"ring", "armor", "ring-armor", "ringarmor");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackerEnchantLvl != 0 && attackEvent.arrow == null && HitCounter.getCharge(attackEvent.attacker, this) == 1) {
			attackEvent.increasePercent += getDamageReduction(attackerEnchantLvl) / 100D;
			HitCounter.setCharge(attackEvent.attacker, this, 0);
		}

		if(defenderEnchantLvl != 0 && attackEvent.arrow != null) {
			HitCounter.setCharge(attackEvent.defender, this, 1);
			attackEvent.multiplier.add(getDamageMultiplier(defenderEnchantLvl));
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage when",
				"&7shot. Deal &c+" + getDamageReduction(enchantLvl) + "% &7damage on", "&7your next melee hit").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return (100D - getDamageReduction(enchantLvl)) / 100;
	}

	public int getDamageReduction(int enchantLvl) {

		return enchantLvl * 15 + 5;
	}
}
