package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
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
		if(defenderEnchantLvl != 0 && attackerMirrorLvl != 0) attackEvent.multiplier.add(Misc.getReductionMultiplier(getReduction(defenderEnchantLvl)));

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(attackerEnchantLvl == 0) return;

		int defenderMirrorLvl = attackEvent.getDefenderEnchantLevel(EnchantManager.getEnchant("mirror"));
		if(defenderMirrorLvl == 0) return;

		attackEvent.increasePercent += getIncrease(attackerEnchantLvl) * defenderMirrorLvl / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + getReduction(enchantLvl) + "% &7damage from mirror",
				"&7users. Deal &c+" + getIncrease(enchantLvl) + "% &7damage per", "&7mirror level on your opponent").getLore();

//		return new ALoreBuilder("&7Deal &c+" + Misc.roundString(getDamage(enchantLvl)) + " &7damage against", "&fMirror &7wearers").getLore();
	}

	public int getReduction(int enchantLvl) {

		return enchantLvl * 5;
	}

	public int getIncrease(int enchantLvl) {

		return enchantLvl * 3 + 1;
	}
}
