package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Lifesteal extends PitEnchant {

	public Lifesteal() {
		super("Lifesteal", false, ApplyType.SWORDS,
				"ls", "lifesteal", "life");
		isUncommonEnchant = true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double damage = attackEvent.getFinalDamageIncrease();
		HealEvent healEvent = pitAttacker.heal(damage * (getHealing(enchantLvl) / 100D) * (attackEvent.fakeHit ? 0.5 : 1));

		if(pitAttacker.stats != null) pitAttacker.stats.lifesteal += healEvent.getEffectiveHeal();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal for &c+" + Misc.roundString(getHealing(enchantLvl)) + "% &7of damage dealt").getLore();
	}

	public double getHealing(int enchantLvl) {

//		return (int) (Math.pow(enchantLvl, 1.1) * 4);
		return enchantLvl * 3 + 1;
	}
}
