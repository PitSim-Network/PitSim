package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Billionaire extends PitEnchant {

	public Billionaire() {
		super("Billionaire", true, ApplyType.SWORDS,
				"bill", "billionaire");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double finalBalance = PitSim.VAULT.getBalance(attackEvent.attacker) - getGoldCost(enchantLvl);
		if(finalBalance < 0) return;
		PitSim.VAULT.withdrawPlayer(attackEvent.attacker, getGoldCost(enchantLvl));

		attackEvent.multiplier.add(getDamageMultiplier(enchantLvl));
		ASound.play(attackEvent.attacker, Sound.ORB_PICKUP, 1, 0.73F);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hits with this sword deal &c" + getDamageMultiplier(enchantLvl) + "x",
				"&cdamage &7but cost &6" + getGoldCost(enchantLvl) + "g").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return (double) Math.round((1 + (double) enchantLvl / 3) * 100) / 100;
	}

	public int getGoldCost(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.75)) * 50 + 50) / 5;
	}
}
