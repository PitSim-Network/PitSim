package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
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
		for(Entity nearby : attackEvent.defender.getNearbyEntities(7, 7, 7)) {
			if(!(nearby instanceof Player) || nearby == attackEvent.defender) continue;
			nearbyPlayers++;
		}
		nearbyPlayers = Math.min(nearbyPlayers, 10);

		double reduction = Math.max(nearbyPlayers * getDamageReduction(enchantLvl), 0);
		attackEvent.multiplier.add(Misc.getReductionMultiplier(reduction));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage per",
				"&7nearby player within 7 blocks", "&7(max 10 players)").getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return Math.min(enchantLvl * 0.5 + 1.5, 100);
	}
}
