package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class CriticallyFunky extends PitEnchant {

	public CriticallyFunky() {
		super("Critically Funky", false, ApplyType.PANTS,
				"criticallyfunky", "critically-funky", "cf", "critfunky", "crit-funky", "crit", "funky");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackEvent.isDefenderPlayer() && defenderEnchantLvl != 0 && Misc.isCritical(attackEvent.getAttacker())) {
			HitCounter.setCharge(attackEvent.getDefenderPlayer(), this, 1);
			attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(defenderEnchantLvl)));
		}

		if(attackEvent.isAttackerPlayer() && attackerEnchantLvl != 0 && HitCounter.getCharge(attackEvent.getAttackerPlayer(), this) == 1) {
			attackEvent.increasePercent += getDamage(attackerEnchantLvl);
			HitCounter.setCharge(attackEvent.getAttackerPlayer(), this, 0);
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		if(enchantLvl == 1) {
			return new PitLoreBuilder(
					"&7Critical hits against you deal &9" +
					Misc.roundString(100 - getReduction(enchantLvl)) + "% &7of the damage they normally would"
			).getLore();

		} else {
			return new PitLoreBuilder(
					"&7Critical hits against you deal &9" +
					Misc.roundString(100 - getReduction(enchantLvl)) + "% &7of the damage they normally would " +
					"and empower your next strike for &c+" + Misc.roundString(getDamage(enchantLvl)) + "&c% &7damage"
			).getLore();
		}
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that decreases " +
				"the damage that critical strikes do against you, and " +
				"makes your next strike do more damage";
	}

	public double getReduction(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
			case 2:
				return 35;
			case 3:
				return 60;

		}
		return 100;
	}

	public double getDamage(int enchantLvl) {
		return enchantLvl * 15 - 15;
	}
}
