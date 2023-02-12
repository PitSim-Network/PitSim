package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class FractionalReserve extends PitEnchant {

	public FractionalReserve() {
		super("Fractional Reserve", false, ApplyType.PANTS,
				"fractionalreserve", "frac", "frac-reserve", "fractional-reserve", "fracreserve");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		double reduction = Math.max((int) Math.log10(attackEvent.getDefenderPitPlayer().gold) + 1, 0);
		attackEvent.multipliers.add(Misc.getReductionMultiplier(reduction * getReduction(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Receive &9-" + decimalFormat.format(getReduction(enchantLvl)) +
				"% &7damage per &6digit &7in your gold"
		).getLore();
	}

	public static double getReduction(int enchantLvl) {
		if(enchantLvl == 1) return 2;
		return enchantLvl * 1.5 + 0.5;
	}
}
