package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.HitCounter;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboDamage extends PitEnchant {

	public ComboDamage() {
		super("Combo: Damage", false, ApplyType.MELEE,
				"combodamage", "cd", "combo-damage", "cdamage");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return;

		attackEvent.increasePercent += getDamage(enchantLvl);

		Sounds.COMBO_PROC.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new PitLoreBuilder(
				"&7Every &e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7strike deals &c+" +
						getDamage(enchantLvl) + "% &7damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that deals " +
				"increased damage every few strikes";
	}

	public int getDamage(int enchantLvl) {
		return (int) (Math.floor(Math.pow(enchantLvl, 1.75)) * 5 + 20);
	}

	public int getStrikes(int enchantLvl) {
		return Math.max(4 - (int) (enchantLvl * 0.5), 1);
	}
}
