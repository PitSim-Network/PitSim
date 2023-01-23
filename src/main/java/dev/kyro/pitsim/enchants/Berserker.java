package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Berserker extends PitEnchant {

	public Berserker() {
		super("Berserker", false, ApplyType.MELEE,
				"berserker");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(!Misc.isCritical(attackEvent.getAttacker()) || Math.random() > getChance(enchantLvl) / 100.0) return;
		attackEvent.multipliers.add(1.5);
		Sounds.BERSERKER.play(attackEvent.getAttackerPlayer());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7You can now critical hit on the",
				"&7ground. &a" + getChance(enchantLvl) + "% chance &7to crit for", "&c50% extra &7damage").getLore();
	}

	public int getChance(int enchantLvl) {
		return enchantLvl * 17 + 24;
	}
}