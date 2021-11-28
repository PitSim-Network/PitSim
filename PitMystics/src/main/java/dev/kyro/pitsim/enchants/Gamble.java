package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Gamble extends PitEnchant {

	public Gamble() {
		super("Gamble", true, ApplyType.SWORDS,
				"gamble", "gam");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.defender) && Regularity.reduceDamage(regLvl)) return;

		if(Math.random() < 0.5) {
			attackEvent.trueDamage += getTrueDamage(enchantLvl);
			Sounds.GAMBLE_YES.play(attackEvent.attacker);
		} else {
			attackEvent.selfVeryTrueDamage += getTrueDamage(enchantLvl);
			Sounds.GAMBLE_NO.play(attackEvent.attacker);
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer.stats != null) pitPlayer.stats.gamble += getTrueDamage(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&d50% chance &7to deal &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + " &7true",
				"&7damage to whoever you hit, or to", "&7yourself").getLore();
	}

	public int getTrueDamage(int enchantLvl) {

		return enchantLvl + 1;
	}
}
