package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Mirror extends PitEnchant {
	public static Mirror INSTANCE;

	public Mirror() {
		super("Mirror", false, ApplyType.PANTS,
				"mirror", "mir");
		isUncommonEnchant = true;
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(attackerEnchantLvl != 0) {
			attackEvent.selfTrueDamage *= Misc.getReductionMultiplier(getReductionPercent(attackerEnchantLvl));
		}

		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(defenderEnchantLvl != 0) {
			attackEvent.trueDamage *= Misc.getReductionMultiplier(getReductionPercent(defenderEnchantLvl));
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		if(enchantLvl >= 3) {
			return new PitLoreBuilder(
					"&7You are immune to true damage"
			).getLore();
		} else {
			return new PitLoreBuilder(
					"&7You take &9" + getReductionPercent(enchantLvl) + "% &7less true damage"
			).getLore();
		}
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that reduces the " +
				"amount of true damage that you take";
	}

	public static int getReductionPercent(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 20;
			case 2:
				return 50;
		}
		return 100;
	}

	public double getReflectionPercent(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 0;
			case 2:
				return 25;
			case 3:
				return 50;

		}
		return 0;
	}
}
