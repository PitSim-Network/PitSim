package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Protection extends PitEnchant {

	public Protection() {
		super("Protection", false, ApplyType.PANTS,
				"prot", "protection", "p");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.multipliers.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage"
		).getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 1.3) * 2) * 2 + 4;
	}
}
