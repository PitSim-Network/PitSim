package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Sharp extends PitEnchant {

	public Sharp() {
		super("Sharp", false, ApplyType.MELEE,
				"sharp", "s");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamageIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamageIncrease(enchantLvl) + "% &7melee damage"
		).getLore();
	}

	public int getDamageIncrease(int enchantLvl) {
		return (int) (Math.pow(enchantLvl, 1.2) * 3 + 1);
	}
}
