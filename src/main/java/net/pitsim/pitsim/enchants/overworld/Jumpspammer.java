package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Jumpspammer extends PitEnchant {

	public Jumpspammer() {
		super("Jumpspammer", false, ApplyType.BOWS,
				"jumpspammer", "jump");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
//		if(!canApply(attackEvent)) return; // needs to be off for this enchant

		if(attackEvent.isAttackerPlayer() && !attackEvent.getAttackerPlayer().isOnGround()) {
			int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
			if(enchantLvl != 0) attackEvent.increasePercent += getDamage(enchantLvl);
		}

		if(attackEvent.isDefenderPlayer() && !attackEvent.getDefenderPlayer().isOnGround()) {
			int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
			if(enchantLvl != 0) attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(enchantLvl)));
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		if(enchantLvl == 1) return new PitLoreBuilder(
				"&7While midair, your arrows deal &c+" + getDamage(enchantLvl) + "% &7damage"
		).getLore();
		return new PitLoreBuilder(
				"&7While midair, your arrows deal &c+" + getDamage(enchantLvl) +
				"% &7damage. While midair, receive &9-" + getReduction(enchantLvl) +
				"% &7damage from melee and ranged attacks"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that decreases " +
				"the damage you take and increases the damage of your arrows when you are in the air";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 8;
	}

	public int getReduction(int enchantLvl) {
		return enchantLvl * 10 - 10;
	}
}
