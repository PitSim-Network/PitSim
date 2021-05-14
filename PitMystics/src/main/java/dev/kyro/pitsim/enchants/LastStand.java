package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LastStand extends PitEnchant {

	public LastStand() {
		super("Last Stand", false, ApplyType.PANTS,
				"laststand", "last", "last-stand", "resistance");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent	) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.defender.getHealth() - attackEvent.getFinalDamage() <= 6) {
			Bukkit.broadcastMessage("6 or below!");
			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.DAMAGE_RESISTANCE, getSeconds(enchantLvl)
					* 20, getAmplifier(enchantLvl) - 1, false, false);
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getAmplifier(enchantLvl)) + "% &7damage").getLore();
	}

	public int getAmplifier(int enchantLvl) {

		return enchantLvl;
	}

	public int getSeconds(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 3;
			case 2:
				return 4;
			case 3:
				return 4;

		}

		return 0;
	}
}
