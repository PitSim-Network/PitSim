package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Chipping extends PitEnchant {

	public Chipping() {
		super("Chipping", false, ApplyType.BOWS,
				"chipping", "chip");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || !attackEvent.getArrow().isCritical()) return;

		attackEvent.trueDamage += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new ALoreBuilder("&7Deals &c" + Misc.getHearts(getDamage(enchantLvl)) + " &7extra true damage").getLore();
	}

	public double getDamage(int enchantLvl) {
		if(enchantLvl == 1) return 0.5;
		return enchantLvl - 1;
	}
}
