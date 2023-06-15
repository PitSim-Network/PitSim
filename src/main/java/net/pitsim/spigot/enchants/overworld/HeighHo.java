package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class HeighHo extends PitEnchant {

	public HeighHo() {
		super("Heigh-Ho", false, ApplyType.PANTS,
				"heighho", "heigh-ho", "hiho", "hi-ho", "antimirror", "nomirror");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);
		int attackerMirrorLvl = attackEvent.getAttackerEnchantLevel(EnchantManager.getEnchant("mirror"));
		if(defenderEnchantLvl != 0 && attackerMirrorLvl != 0)
			attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(defenderEnchantLvl)));

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(attackerEnchantLvl == 0) return;

		int defenderMirrorLvl = attackEvent.getDefenderEnchantLevel(EnchantManager.getEnchant("mirror"));
		if(defenderMirrorLvl == 0) return;

		attackEvent.increasePercent += getIncrease(attackerEnchantLvl) * defenderMirrorLvl;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Receive &9-" + getReduction(enchantLvl) + "% &7damage from mirror users. Deal &c+" +
				getIncrease(enchantLvl) + "% &7damage per mirror level on your opponent"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that decreases " +
				"the damage you take and increases the damage you deal vs players wearing &9" +
				Mirror.INSTANCE.getDisplayName(false, true);
	}

	public int getReduction(int enchantLvl) {
		return 10;
	}

	public int getIncrease(int enchantLvl) {
		return enchantLvl * 5;
	}
}
