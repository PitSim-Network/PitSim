package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class NewDeal extends PitEnchant {

	public NewDeal() {
		super("New Deal", false, ApplyType.PANTS,
				"newdeal", "new-deal", "nd", "new", "deal");
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitEnchant bill = EnchantManager.getEnchant("billionaire");
		int billLevel = attackEvent.getAttackerEnchantLevel(bill);

		if(billLevel != 0) attackEvent.getAttackerEnchantMap().remove(bill);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitEnchant bill = EnchantManager.getEnchant("billionaire");
		int billLevel = attackEvent.getAttackerEnchantLevel(bill);

		attackEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 1) {
			return new ALoreBuilder("&7You are immune to &6Billionaire").getLore();
		} else {
			return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage and you are",
					"&7immune to &6Billionaire").getLore();
		}
	}

	public double getDamageReduction(int enchantLvl) {

		return (enchantLvl - 1) * 4;
	}
}
