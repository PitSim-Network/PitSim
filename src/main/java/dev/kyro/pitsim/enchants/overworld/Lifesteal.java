package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Lifesteal extends PitEnchant {

	public Lifesteal() {
		super("Lifesteal", false, ApplyType.MELEE,
				"ls", "lifesteal", "life");
		isUncommonEnchant = true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		double damage = attackEvent.getFinalPitDamageIncrease();
		double healing = damage * (getHealing(enchantLvl) / 100D);
		HealEvent healEvent = pitAttacker.heal(healing);

		if(pitAttacker.stats != null) pitAttacker.stats.lifesteal += healEvent.getEffectiveHeal();
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Heal for &c+" + Misc.roundString(getHealing(enchantLvl)) + "% &7of damage dealt"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that heals you " +
				"for a percentage of the damage that you deal. Despite its low value, this enchant is one of the " +
				"most important when fighting other players";
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl * 3 - 2;
	}
}
