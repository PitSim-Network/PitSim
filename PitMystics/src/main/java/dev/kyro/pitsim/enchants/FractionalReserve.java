package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class FractionalReserve extends PitEnchant {

	public FractionalReserve() {
		super("Fractional Reserve", false, ApplyType.PANTS,
				"fractionalreserve", "frac", "frac-reserve", "fractional-reserve", "fracreserve");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		int reduction = (int) Math.min(PitSim.VAULT.getBalance(attackEvent.attacker) / 10000, getMaxDamageReduction(enchantLvl));
		attackEvent.multiplier.add(Misc.getReductionMultiplier(reduction));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-1% damage per",
				"&610,000g &7you have (&9-" + getMaxDamageReduction(enchantLvl) + "%", "&7max)").getLore();
	}

	public double getMaxDamageReduction(int enchantLvl) {

		return (int) Math.max(Math.floor(Math.pow(enchantLvl, 1.65) * 3) + 12, 0);
	}
}
