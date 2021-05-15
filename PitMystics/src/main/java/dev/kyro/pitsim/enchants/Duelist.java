package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.controllers.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Duelist extends PitEnchant {

	public Duelist() {
		super("Duelist", false, ApplyType.SWORDS,
				"duel", "duelist", "dualist", "dual");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;


		int attackerEnchantLvl = attackEvent.getAttackerEnchantLevel(this);
		int defenderEnchantLvl = attackEvent.getDefenderEnchantLevel(this);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);


		if(defenderEnchantLvl != 0 && attackEvent.defender.isBlocking()) {
			HitCounter.incrementCounter(pitPlayer.player, this);
			if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 2)) return;

			HitCounter.setCharge(attackEvent.defender, this, 1);
			attackEvent.defender.setHealth(Math.min(attackEvent.defender.getHealth()
					+ getHealing(defenderEnchantLvl), attackEvent.defender.getMaxHealth()));
		}


		if(attackerEnchantLvl != 0 && HitCounter.getCharge(attackEvent.attacker, this) == 1) {
			attackEvent.increasePercent += getDamage(attackerEnchantLvl) / 100D;
			HitCounter.setCharge(attackEvent.attacker, this, 0);
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Blocking two hits from the same", "&7player empowers your next strike",
				"&7against them for &c+" + getDamage(enchantLvl) + "% &7damage", "&7and heals for &c" + Misc.getHearts(getHealing(enchantLvl))).getLore();
	}

	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 20;
			case 2:
				return 40;
			case 3:
				return 75;

		}
		return 0;
	}

	public int getHealing(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;

		}
		return 0;
	}
}
