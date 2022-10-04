package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboHeal extends PitEnchant {

	public ComboHeal() {
		super("Combo: Heal", false, ApplyType.MELEE,
				"comboheal", "ch", "combo-heal", "cheal");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer) return;
		if(!canApply(attackEvent)) return;
		PitPlayer pitAttacker = attackEvent.attackerPitPlayer;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.fakeHit) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.defender) && Regularity.skipIncrement(regLvl)) return;

		HitCounter.incrementCounter(pitAttacker.player, this);
		if(!HitCounter.hasReachedThreshold(pitAttacker.player, this, 4)) return;

		pitAttacker.heal(getHealing(enchantLvl));
		pitAttacker.heal(getHealing(enchantLvl), HealEvent.HealType.ABSORPTION, 8);

		Sounds.COMBO_PROC.play(attackEvent.attacker);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every &efourth &7strike heals",
				"&c" + Misc.getHearts(getHealing(enchantLvl)) + " &7and grants &6" + Misc.getHearts(getHealing(enchantLvl))).getLore();
	}

	public double getHealing(int enchantLvl) {

		return enchantLvl * 0.8;
	}
}
