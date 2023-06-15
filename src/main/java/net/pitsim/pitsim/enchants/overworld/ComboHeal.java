package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.HitCounter;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.events.HealEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
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
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		HitCounter.incrementCounter(pitAttacker.player, this);
		if(!HitCounter.hasReachedThreshold(pitAttacker.player, this, 4)) return;

		pitAttacker.heal(getHealing(enchantLvl));
		HealEvent healEvent = pitAttacker.heal(getAbsorption(enchantLvl), HealEvent.HealType.ABSORPTION, 8, this);

		Sounds.COMBO_PROC.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new PitLoreBuilder("&7Every &efourth &7strike heals &c" +
				Misc.getHearts(getHealing(enchantLvl)) + " &7and grants &6" + Misc.getHearts(getAbsorption(enchantLvl))
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that heals " +
				"you and gives you absorption every few strikes";
	}

	public double getHealing(int enchantLvl) {

		return enchantLvl * 0.8;
	}

	public double getAbsorption(int enchantLvl) {

		return enchantLvl * 0.8;
	}
}
