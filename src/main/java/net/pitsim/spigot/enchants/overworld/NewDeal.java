package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
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

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that makes you " +
				"immune to " + Billionaire.INSTANCE.getDisplayName(false, true) +
				" &7but also makes you take very true damage on every hit";
	}

	public double getDamageReduction(int enchantLvl) {

		return (enchantLvl - 1) * 4;
	}

	public double getTrueDamage(int enchantLvl) {

		return Math.max(1 - enchantLvl * 0.2, 0);
	}
}
