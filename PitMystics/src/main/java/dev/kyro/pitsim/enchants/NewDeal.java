package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.pitevents.Juggernaut;
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

		if(billLevel != 0 && Juggernaut.juggernaut != attackEvent.attacker) attackEvent.getAttackerEnchantMap().remove(bill);
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
	public List<String> getDescription(int enchantLvl) {

//		if(enchantLvl == 1) {
			return new ALoreBuilder("&7You are immune to &6Billionaire&7,",
					"&7but take &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + " &7very true", "&7damage when hit").getLore();
//		} else {
//			return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage and you are",
//					"&7immune to &6Billionaire").getLore();
//		}
	}

	public double getDamageReduction(int enchantLvl) {

		return (enchantLvl - 1) * 4;
	}

	public double getTrueDamage(int enchantLvl) {

		return Math.max(1.2 - enchantLvl * 0.2, 0);
	}
}
