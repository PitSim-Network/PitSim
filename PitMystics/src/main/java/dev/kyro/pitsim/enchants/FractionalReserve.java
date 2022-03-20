package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
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
		if(!attackEvent.defenderIsPlayer) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		double reduction = Math.max((int) Math.log10(PitSim.VAULT.getBalance(attackEvent.defenderPlayer)) + 1, 0);
		attackEvent.multipliers.add(Misc.getReductionMultiplier(reduction * getReduction(enchantLvl)));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new ALoreBuilder("&7Receive &9-" + decimalFormat.format(getReduction(enchantLvl)) + "% &7damage per",
				"&6digit &7in your gold").getLore();
	}

	public static double getReduction(int enchantLvl) {
		if(enchantLvl == 1) return 2;
		return enchantLvl * 2 - 0.5;
	}
}
