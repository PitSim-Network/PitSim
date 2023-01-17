package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
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
			if(enchantLvl != 0) attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
		}

		if(attackEvent.isDefenderPlayer() && !attackEvent.getDefenderPlayer().isOnGround()) {
			int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
			if(enchantLvl != 0) attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(enchantLvl)));
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		if(enchantLvl == 1) return new ALoreBuilder("&7While midair, your arrows deal",
				"&c+" + getDamage(enchantLvl) + "% &7damage").getLore();
		return new ALoreBuilder("&7While midair, your arrows deal",
				"&c+" + getDamage(enchantLvl) + "% &7damage. While midair,",
				"&7receive &9-" + getReduction(enchantLvl) + "% &7damage from melee", "&7and ranged attacks").getLore();
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 8;
	}

	public int getReduction(int enchantLvl) {
		return enchantLvl * 10 - 10;
	}
}
