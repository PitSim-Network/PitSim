package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Peroxide extends PitEnchant {

	public Peroxide() {
		super("Peroxide", false, ApplyType.PANTS,
				"pero", "peroxide", "regeneration", "regen");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent	) {

		int enchantLvl = EnchantManager.getEnchantLevel(attackEvent.defender, this);
		if(enchantLvl == 0) return;

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.REGENERATION, getSeconds(enchantLvl)
				* 20, getAmplifier(enchantLvl) - 1, false, false);

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getAmplifier(enchantLvl)) + "% &7damage").getLore();
	}

	public int getAmplifier(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
				return 1;
			case 3:
				return 2;

		}

		return 0;

	}

	public int getSeconds(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 5;
			case 2:
				return 8;
			case 3:
				return 8;

		}

		return 0;
	}
}
