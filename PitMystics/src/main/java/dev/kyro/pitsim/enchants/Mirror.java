package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Mirror extends PitEnchant {

	public Mirror() {
		super("Mirror", false, ApplyType.PANTS,
				"mirror", "mir");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = EnchantManager.getEnchantLevel(attackEvent.defender, this);
		if(enchantLvl == 0) return;

		PitEnchant perun = EnchantManager.getEnchant("perun");
		PitEnchant gamble = EnchantManager.getEnchant("gamble");
		PitEnchant mirror = EnchantManager.getEnchant("mirror");

		int perunLevel = EnchantManager.getEnchantLevel(attackEvent.getAttackerEnchantMap(), perun);
		int gambleLevel = EnchantManager.getEnchantLevel(attackEvent.getAttackerEnchantMap(), gamble);
		int mirrorLevel = EnchantManager.getEnchantLevel(attackEvent.getAttackerEnchantMap(), mirror);

		if(billLevel != 0) attackEvent.getAttackerEnchantMap().remove(bill);

		attackEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

	if(enchantLvl == 0) {
		return new ALoreBuilder("&7You are immune to &6Billionaire").getLore();
	} else {
		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage and you are",
				"&7immune to &6Billionaire").getLore();
	}

	}

	public double getDamageReduction(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 0;
			case 2:
				return 4;
			case 3:
				return 8;

		}

		return 0;

	}
}
