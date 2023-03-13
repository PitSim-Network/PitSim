package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class NotGladiator extends PitEnchant {

	public NotGladiator() {
		super("\"Not\" Gladiator", false, ApplyType.PANTS,
				"notglad", "notgladiator", "not-glad", "not-gladiator", "ng");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		int nearbyPlayers = 0;
		for(Entity nearby : attackEvent.getDefender().getNearbyEntities(7, 7, 7)) {
			if(!(nearby instanceof Player) || nearby == attackEvent.getDefender()) continue;
			nearbyPlayers++;
		}
		nearbyPlayers = Math.min(nearbyPlayers, 10);

		double reduction = Math.max(nearbyPlayers * getDamageReduction(enchantLvl), 0);
		attackEvent.multipliers.add(Misc.getReductionMultiplier(reduction));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) +
						"% &7damage per nearby player within 7 blocks (max 10 players)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that decreases " +
				"the damage you take when there are more player and/or bots nearby";
	}

	public double getDamageReduction(int enchantLvl) {

		return Math.min(enchantLvl * 0.5 + 1.5, 100);
	}
}
