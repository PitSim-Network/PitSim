package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class NewDeal extends PitEnchant {

	public NewDeal() {
		super("New Deal", false, ApplyType.PANTS,
				"newdeal", "new-deal", "nd", "new", "deal");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitEnchant bill = EnchantManager.getEnchant("billionaire");
		int billLevel = attackEvent.getAttackerEnchantLevel(bill);

		if(billLevel != 0) attackEvent.getAttackerEnchantMap().remove(bill);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

//		attackEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
		attackEvent.veryTrueDamage += getTrueDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7You are immune to &6Billionaire&7, but take &c" +
				Misc.getHearts(getTrueDamage(enchantLvl)) + " &7very true damage when hit"
		).getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return (enchantLvl - 1) * 4;
	}

	public double getTrueDamage(int enchantLvl) {

		return Math.max(1.1 - enchantLvl * 0.2, 0);
	}
}
