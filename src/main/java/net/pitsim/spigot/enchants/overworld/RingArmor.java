package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.HitCounter;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class RingArmor extends PitEnchant {

	public RingArmor() {
		super("Ring Armor", false, ApplyType.PANTS,
				"ring", "armor", "ring-armor", "ringarmor");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		if(attackEvent.isAttackerPlayer() && attackerEnchantLvl != 0 && attackEvent.getArrow() == null &&
				HitCounter.getCharge(attackEvent.getAttackerPlayer(), this) == 1) {
			attackEvent.increasePercent += getDamageIncrease(attackerEnchantLvl);
			HitCounter.setCharge(attackEvent.getAttackerPlayer(), this, 0);
		}

		if(defenderEnchantLvl != 0 && attackEvent.getArrow() != null) {
			HitCounter.setCharge(attackEvent.getDefenderPlayer(), this, 1);
			attackEvent.multipliers.add(getDamageMultiplier(defenderEnchantLvl));
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		if(enchantLvl == 1) return new PitLoreBuilder(
				"&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage when shot"
		).getLore();

		return new PitLoreBuilder(
				"&7Receive &9-" + getDamageReduction(enchantLvl) + "% &7damage when shot. Deal &c+" +
				getDamageIncrease(enchantLvl) + "% &7damage on your next melee hit"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that decreases " +
				"the damage that arrows deal to you, and makes you do more damage after being shot by an arrow";
	}

	public double getDamageMultiplier(int enchantLvl) {
		return (100D - getDamageReduction(enchantLvl)) / 100;
	}

	public int getDamageReduction(int enchantLvl) {
		return 60;
	}

	public int getDamageIncrease(int enchantLvl) {
		return enchantLvl * 15 + 25;
	}
}
