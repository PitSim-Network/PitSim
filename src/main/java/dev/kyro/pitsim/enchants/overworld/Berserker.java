package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Berserker extends PitEnchant {

	public Berserker() {
		super("Berserker", false, ApplyType.MELEE,
				"berserker", "bez", "bes");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(!Misc.isCritical(attackEvent.getAttacker()) || Math.random() > getChance(enchantLvl) / 100.0) return;
		attackEvent.multipliers.add(1.5);
		Sounds.BERSERKER.play(attackEvent.getAttackerPlayer());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7You can now critical hit on the ground. &a" + getChance(enchantLvl) +
						"% chance &7to crit for &c50% extra &7damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"the chance to land critical strikes while on the ground";
	}

	public int getChance(int enchantLvl) {
		return enchantLvl * 17 + 24;
	}
}