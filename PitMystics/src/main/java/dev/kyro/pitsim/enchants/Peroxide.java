package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
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
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent	) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, 51);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.REGENERATION, getDuration(enchantLvl),
				getAmplifier(enchantLvl) - 1, false, false);

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain &cRegen " + AUtil.toRoman(getAmplifier(enchantLvl)) + " &7(" +
				getDuration(enchantLvl)/20 + "&7s) when hit").getLore();
	}

	public int getAmplifier(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 1);
	}

	public int getDuration(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 1.5) * 50 + 49;
	}
}
