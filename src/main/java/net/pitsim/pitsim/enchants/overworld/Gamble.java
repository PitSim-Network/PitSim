package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Gamble extends PitEnchant {

	public Gamble() {
		super("Gamble", true, ApplyType.MELEE,
				"gamble", "gam");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.reduceDamage(regLvl)) return;

		if(Math.random() < 0.5) {
			attackEvent.trueDamage += getTrueDamage(enchantLvl);
			Sounds.GAMBLE_YES.play(attackEvent.getAttacker());
		} else {
			attackEvent.selfVeryTrueDamage += getTrueDamage(enchantLvl);
			Sounds.GAMBLE_NO.play(attackEvent.getAttacker());
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());
		pitPlayer.stats.gamble += getTrueDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&d50% chance &7to deal &c" + Misc.getHearts(getTrueDamage(enchantLvl)) +
				" &7true damage to whoever you hit, or to yourself"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that has a " +
				"50-50 chance to deal true damage to you or your opponent";
	}

	public int getTrueDamage(int enchantLvl) {

		return enchantLvl + 1;
	}
}
