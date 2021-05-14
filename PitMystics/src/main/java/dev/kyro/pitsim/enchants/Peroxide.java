package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
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

		Cooldown cooldown = getCooldown(attackEvent.attacker, getCooldown(enchantLvl));
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.REGENERATION, getDuration(enchantLvl),
				getAmplifier(enchantLvl) - 1, false, false);

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getAmplifier(enchantLvl)) + "% &7damage").getLore();
	}

	public int getAmplifier(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
			case 2:
				return 1;
			case 3:
				return 2;
		}
		return 0;
	}

	public int getDuration(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 99;
			case 2:
				return 149;
			case 3:
				return 149;
		}
		return 0;
	}

	public int getCooldown(int enchantLvl) {
		switch(enchantLvl) {
			case 1:
				return 51;
			case 2:
				return 51;
			case 3:
				return 51;
		}
		return 0;
	}
}
